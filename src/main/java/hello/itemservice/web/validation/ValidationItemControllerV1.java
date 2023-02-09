package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v1/items")
@RequiredArgsConstructor
public class ValidationItemControllerV1 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v1/items";
    }

    //EX) http://127.0.0.1:8080/basic/items/1
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);// //itemId 1로 넘어온 값으로 조회
        model.addAttribute("item", item);
        return "validation/v1/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v1/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        //model.addAttribute("item", item)

        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();//화면에서 빨간글씨로 보여주려고~


        //검증로직 만들기🔽
        if(!StringUtils.hasText(item.getItemName())){//hasText 빈값이 아니면 true를 반환(글자가 있는지 확인)
            errors.put("itemName","상품 이름은 필수입니다.");
        }

        if(item.getPrice() ==null || item.getPrice() < 1000 || item.getPrice() >100000){
            errors.put("price","가격은 1,000 ~ 1,000,000까지 허용합니다.");
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (!errors.isEmpty()) {//에러가 있다면 FALSE를 내보지? (빈값이 아니니까), 거기에 !을 달았으니까 TRUE로 변경되는거야 ( 사실 좋은 코드는 아니래, 부정에 부정이니까 해석하기가;;)
            log.info("errors = {} ", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);//상세보기 페이지에서 고객에게 저장됐다고 알리기 위해서.
        return "redirect:/basic/items/{itemId}"; //상품저장 후 인코딩되어 넘어간다. RedirectAttributes를 사용하면 URL 인코딩도 해주고, @pathVarible 쿼리 파라미터까지 처리해준다.
        //redirect:/basic/items/{itemId}
        //pathVariable 바인딩: {itemId}
        //나머지는 쿼리 파라미터로 처리: ?status=true
        //치환이 되지 않는거는 쿼리 파라미터로 넘어간다~
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v1/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v1/items/{itemId}";
    }

}

