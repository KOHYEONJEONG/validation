package hello.itemservice.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import hello.itemservice.exhandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "hello.itemservice.web.validation.api")
public class APIExceptionController {

    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ExceptionResponse>  handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        /*StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("] \n");
        }

        return builder.toString();
        */

        //https://my-codinglog.tistory.com/entry/%EC%98%88%EC%99%B8-%EC%B2%98%EB%A6%AC
        ExceptionResponse response = ExceptionResponse.create(HttpStatus.BAD_REQUEST.value(),e.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult exHandle(HttpMessageNotReadableException e) {
        log.error("[exceptionHandle] ex", e);
        String errorMessage = extractErrorMessage(e);//어떤 필드에서 에러가 났는지.
        return new ErrorResult("타입오류", errorMessage);
    }


    private String extractErrorMessage(HttpMessageNotReadableException ex) {
        if (ex.getRootCause() instanceof InvalidFormatException) {

            /*InvalidFormatException은 JSON 데이터를 파싱하는 동안 발생한 타입 변환 오류를 나타내는 예외입니다.
            이 예외에서 getPath() 메서드를 호출하면 발생한 오류의 경로 정보가 반환됩니다.
            경로 정보는 JsonMappingException.Reference 객체의 리스트로 표현되며,
            각 객체는 오류가 발생한 필드와 관련된 정보를 포함합니다.*/
            InvalidFormatException rootCause = (InvalidFormatException) ex.getRootCause();
            String fieldName = rootCause.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));

            /*
                처음에 걸린 타입 필드명이 출력된다.
                ㄴ price랑 quantity 둘다 해봤는데 price에서 걸리면 price 폴더명만 나옴.
            */


            String errorMessage = "Invalid value for field '" + fieldName + "'.";
            return errorMessage;
        }
        return "Invalid request body.";
    }

    @Getter
    static class ExceptionResponse{
        private int status;
        private List<FieldException> exceptions;
        @Builder
        public ExceptionResponse(int status, List<FieldException> exceptions) {
            this.status = status;
            this.exceptions = (exceptions == null)?new ArrayList<>():exceptions;
        }

        public static ExceptionResponse create(int status, BindingResult bindingResult){
            return ExceptionResponse.builder()
                    .status(status)
                    .exceptions(FieldException.create(bindingResult)).build();
        }
    }

    @Getter
    @AllArgsConstructor
    static class FieldException{
        private String field;
        private String value;
        private String reason;


        private static List<FieldException> create(BindingResult bindingResult){
            List<FieldError> fieldException = bindingResult.getFieldErrors(); //BindingResult의 getFieldErrors() 메서드를 사용하여 List 생성
            return fieldException.stream()
                    .map(error->new FieldException(
                            error.getField(),
                            (error.getRejectedValue() == null)?null:error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList()); //해당 List의 각 원소들에 대해 FieldException 객체가 필요로 하는 값을 각 필드로 매핑
        }
    }
}
