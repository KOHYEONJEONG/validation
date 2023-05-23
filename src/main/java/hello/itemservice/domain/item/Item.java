package hello.itemservice.domain.item;

import lombok.Data;
import org.apache.logging.log4j.message.Message;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/** 등록용 Form과 수정 Form은 따로 만들어야 하는게 맞다
 *  ㄴ 유효성 검사하기위해 애너테이션을 넣다보면 미관상 복잡해지기 때문이다.
 *   그래서 Item 클래스에 유효성검사는 주석처리 해두었다.
 * */
@Data
public class Item {

    /** groups 속성을 넣어줘야지 등록 페이지와 수정페이지에 검증 조건을 나뉠 수 있다.
     * 그전에 인터페이스를 만들어야 한다.
     * ㄴ SaveCheck
     * ㄴ UpdateCheck
     * */

    //@NotNull(groups = UpdateCheck.class) //(수정 요구사항 추가), 등록버튼을 눌러야 ID가 생성된다. 그래서 @NotNull만 사용하면 안된다.
    private Long id;

   // @NotBlank(message = "공백x")
   // @NotBlank(groups = {SaveCheck.class, UpdateCheck.class}, message = "공백x")
    private String itemName;

    //@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
   // @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    //@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
   // @Max(value = 9999, groups = {SaveCheck.class})
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
