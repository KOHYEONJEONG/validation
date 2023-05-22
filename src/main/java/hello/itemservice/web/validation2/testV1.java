package hello.itemservice.web.validation2;

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
@RequestMapping("/validationTest/v1/items")
@RequiredArgsConstructor
public class testV1 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v1/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v1/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        //처음에는 빈값으로 넘어감.
        model.addAttribute("item", new Item());
        return "validation/v1/addForm";
    }
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes){

        Map<String ,String> errors = new HashMap<>();

        //검증로직
        if(!StringUtils.hasText(item.getItemName())){
            errors.put("itemName","상품 이름은 필수입니다.");
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000 ) {
            errors.put("price", "가격은 1,000 ~ 1,000,000까지 허용합니다.");
        }

        if(item.getPrice() == null || item.getQuantity() >= 9999){
            errors.put("quantity","수량은 최대 9,999까지 허용합니다.");
        }

        //특정 필드가 아닌 복합 툴 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                errors.put("globalError","가격*수량의 합은 10,000원 이상이어야 합니다. 현재 값 = "+resultPrice);
            }
        }

        Item saveItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", saveItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

}
