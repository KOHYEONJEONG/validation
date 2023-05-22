package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import static org.assertj.core.api.Assertions.*;

public class MessageCodesResolverTest {
    //BindingResult 테스트
    
    //인터페이스
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();//오른쪽은 구현체

    @Test
    void messageCodeResolverObject(){


       String[] messageCodes =  codesResolver.resolveMessageCodes("required","item");
        //new ObjectError("item", new String[]{"required.item", "required"}); <-- 두번째 파라미터 부분이 messageCodes가 들어가는거다.

        for(String messageCode : messageCodes){
            System.out.println("messageCode = "+messageCode);
        }
        assertThat(messageCodes).containsExactly("required.item", "required");
        /*
        * messageCode = required.item
          messageCode = required
        * */


    }

    @Test
    void messageCodeResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required","item", "itemName", String.class);
        //bindingResult.rejectValue("itemName", "required");
        //new FieldError("item","itemName", null, false, messageCodes, null, null);

        for(String messageCode : messageCodes){
            System.out.println("messageCode = "+messageCode);
        }


        assertThat(messageCodes).containsExactly(
                //level 1부터 먼저 우선순위
                "required.item.itemName",//디데일하게
                "required.itemName",//객체명 생략
                "required.java.lang.String",//required.타입 <- 문자를 필수 입력하셔야합니다로 공통 메시지로 만들 수 있겠지?(숫자는 숫자로 입력하셔야합니다.)
                "required");

        /*
        * messageCode = required.item.itemName (required.객체명.필드명 <-- 제일 구체젝)
        messageCode = required.itemName         (required.필드명      <-- 두번쨰 디테일)
        messageCode = required.java.lang.String (문자인지 )
        messageCode = required                  (가장 넓은 범위)
        * */

    }
}
