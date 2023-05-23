package hello.itemservice.web.validation.api;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {



    /**@Valid , @Validated 는 HttpMessageConverter ( @RequestBody )에도 적용할 수 있다.
     * ㄴ @RequestBody 는 HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후
     * 단계 자체가 진행되지 않고 예외가 발생한다. 컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.
     * ㄴ APIExceptionController에서 해당 예외 처리를 만들었다.
     * */
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form) {//, BindingResult bindingResult <-- 이게 있으면 예외러치 만든게 제대로 실행 안됌.

        log.info("API 컨트롤러 호출");

    //   if (bindingResult.hasErrors()) {
    //       log.info("검증 오류 발생 errors={}", bindingResult);
    //       return bindingResult.getAllErrors();
    //   }

//   {"itemName":"hello", "price":"10", "quantity": 10}
//   오류가 발생하면 아래와 같이 출력된다. 정상적인 값이 아니여서 유효성 검사가 안되며, 아래와 같이 긴 문구가 출력된다.
            //api 에러가 발생히 예외 메시지를 생성해서 보내줘야 한다.( api 예외처리를 해주면 된다, APIExceptionController.java에서 에러처리 진행함)
//            [
            //            {
            //                "codes": [
            //                "Range.itemSaveForm.price",
            //                        "Range.price",
            //                        "Range.java.lang.Integer",
            //                        "Range"
            //        ],
            //                "arguments": [
            //                {
            //                    "codes": [
            //                    "itemSaveForm.price",
            //                            "price"
            //                ],
            //                    "arguments": null,
            //                        "defaultMessage": "price",
            //                        "code": "price"
            //                },
            //                1000000,
            //                        1000
            //        ],
            //                "defaultMessage": "1000에서 1000000 사이여야 합니다",
            //                    "objectName": "itemSaveForm",
            //                    "field": "price",
            //                    "rejectedValue": 10,
            //                    "bindingFailure": false,
            //                    "code": "Range"
            //            }
//              ]


        log.info("성공 로직 실행");
        return form;
    }
}
