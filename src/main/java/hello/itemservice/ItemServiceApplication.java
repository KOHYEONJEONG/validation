package hello.itemservice;

import hello.itemservice.web.validation.ItemValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	/** 글로벌 설정 - 모든 컨트롤러에 다 적용 , @Validated 적어줘야함. 하지만 글러벌 설정할 일은 많이 없음.*/
	//@Override
	//public Validator getValidator() {
	//	return new ItemValidator();
	//}
}
