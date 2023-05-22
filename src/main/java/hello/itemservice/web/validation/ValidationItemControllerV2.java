package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.properties.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items") //ctrl+r 한번에 값 변경
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder //해당 컨트롤러에만 영향을 준다.(글로벌 설정은 별도로 해야한다.)
    public void init(WebDataBinder dataBinder) {
        //WebDataBinder : 컨트롤러 요청될때 새롭게 만들어진다. 해당 컨트롤러에 메서드들을 호출하면 자동으로 검증기 적용
        //이제 사용할 메서드 맨 앞에 @Validated를 넣어주면 된다.
        dataBinder.addValidators(itemValidator);
        //dataBinder.addValidators(검증기); <-- 검증기를 여러개 추가해도 된다. 왜? supports()에서 해당 객체가 넘어오면 true를 리턴하고 그래야하만 validate() 함수가 실행된다.
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {//vo명, 필드명, 거절된 값(사용자 입력값), 데이터 넘어온게 실패인지(false로 두자), null, null, 에러메시지
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수 입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null ,null, "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",null ,null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        //에러 메시지를 errors.properties 파일에 모아두어 파라미터를 던지자.
        //4번째 인자에는 에러메시지 new String[]{"key"}
        //5번째 인자값에 파라미터를 ~ , new Object[]{} 중괄호 안에 파라미터를 넘기면됨. (파라미터가 2개이상이면 쉼표로 구분)

        log.info("objectName={}",bindingResult.getObjectName());//item을 가리킴
        log.info("target={}",bindingResult.getTarget());//Item객체에 toString()이 출력됨.

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
           // bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
            bindingResult.rejectValue("itemName","required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {                                                            // new Object[]{1000, 1000000} <-- 파라미터 2개라서 쉼표로 구분
            //bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
            bindingResult.rejectValue("price","range",new Object[]{1000,1000000},null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
           // bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"} ,new Object[]{9999}, null));
            bindingResult.rejectValue("quantity","max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"} ,new Object[]{10000, resultPrice}, null));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


    /** (개발자에 욕심은 끝이 없다 훨씬 간결해졌다.)
     * rejectValue를 사용하면 ObjectError, FieldError가 다 이뤄진다. */
//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /*
        if(bindingResult.hasErrors()){
            //에러가 생긴다면(ex 숫자 필드에 문자열이 들어오는 거지)
            log.info("errors={}", bindingResult);
            //errors=org.springframework.validation.BeanPropertyBindingResult: 2 errors
           return "validation/v2/addForm";
        }*/

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

       /**검증로직(아래 IF문 191~194 같은 문장)
        * 단, Empty와 공백 같은 단순한 기능만 제공한다.
        * */
        //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName","required");

        //rejectValue : 거절한 값
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            /**
             # ObjectError (reject("totalPriceMin"))
             Error in object 'item': codes [totalPriceMin.item,totalPriceMin]; arguments [10000,2000]; default message [null] */

            /**
             # FieldError (rejectValue("itemName", "required"))
             2023-05-22 23:32:47.469  INFO 9292 --- [nio-8090-exec-6] h.i.w.v.ValidationItemControllerV2       : errors=org.springframework.validation.BeanPropertyBindingResult: 3 errors
             Field error in object 'item' on field 'itemName': rejected value []; codes [required.item.itemName,required.itemName,required.java.lang.String,required]; arguments []; default message [null]
             Field error in object 'item' on field 'price': rejected value [null]; codes [range.item.price,range.price,range.java.lang.Integer,range]; arguments [1000,10000000]; default message [null]
             Field error in object 'item' on field 'quantity': rejected value [null]; codes [max.item.quantity,max.quantity,max.java.lang.Integer,max]; arguments [9999]; default message [null]*/
            return "validation/v2/addForm";
        }

        /**
         * #정리
         * 1. rejectValue() 호출
         * 2. MessageCodesResolver() 를 사용해서 검증 오류 코드로 메시지 코드들을 생성
         * 3. new FieldError()를 생성하면서 메시지 코드들을 보관
         * 4. th:errors에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고 노출
         *
         * */

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /** ItemValidator.java에서 검증을 관리할거다. 유지보수하기 좋아진다. */
        itemValidator.validate(item, bindingResult);

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * @Validated : 검증기를 편리하게 도입
     * ㄴ validator를 직접 호출하는 부분이 사라지고, 대신에 검증 대상 앞에 @Validated가 붙었다.
     * */
    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // @Validated @ModelAttribute Item item, BindingResult bindingResult <-- 작성 순서 중요!

        //(아래)이 컨트롤러 맨상단에 적음.
//        @InitBinder
//        public void init(WebDataBinder dataBinder) {
//            dataBinder.addValidators(itemValidator);
//        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }


}

