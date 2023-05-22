package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component //Bean 등록
public class ItemValidator implements Validator { //implements Validator

    /** 검증하는 일을 따로 빼두면 유지보수하기 편해진다.
     * ㄴ 스프링은 검증을 체계적으로 제공하기 위해서 Validator 인터페이스를 제공한다.
     * */

    @Override
    public boolean supports(Class<?> clazz) {

        //검증기가 여러개 있을때 어떻게 구분하냐면
        //아래 Item클래스 객체가 넘어오면 true 아니면 false
        //true이면 아래 validate()가 실행되고, 아니면 실행 안된다.
        return Item.class.isAssignableFrom(clazz); //isAssignableFrom : 자식, 부모 클래스 다 검증하겠다.
    }

    @Override
    public void validate(Object target, Errors errors) { //Errors는 BindingResult에 부모

        //다형성 : 부모는 자식을 넣을 수 있지만, 자식은 부모를 담을 수 없다.
        Item item = (Item) target;//캐스팅

        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
