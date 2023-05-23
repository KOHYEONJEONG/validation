package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationTest2 {

    /** 외울 필요 x, 따라만 해보기*/
    @Test
    void beanValidation(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();


        Item item = new Item();
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = "+violation);
            System.out.println("violation.message = "+violation.getMessage());

//
//            violation = ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.Max.message}'}
//            violation.message = 9999 이하여야 합니다 <--hibernate에서 기본적으로 제공하는 메시지(변경도 가능하다.)

//            violation = ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{javax.validation.constraints.NotBlank.message}'}
//            violation.message = 공백일 수 없습니다   <--hibernate에서 기본적으로 제공하는 메시지(변경도 가능하다.)

//            violation = ConstraintViolationImpl{interpolatedMessage='1000에서 1000000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
//            violation.message = 1000에서 1000000 사이여야 합니다 <--hibernate에서 기본적으로 제공하는 메시지(변경도 가능하다.)

        }
    }

}
