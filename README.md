<h1> validation2</h1>

<h4>스프링이 제공하는 유효성 검사 BindlingResult</h4>
1. new FieldError()
<br/>
1-1. new ObjectError()
<br/><br/>

2.rejectValue()<br/>
2-1.reject()<br/><br/>

3.ItemValidator 클래스를 만들어서 검증로직 관리하기<br/>
3-1.@InitBinder와 WebDataBinder <br/>
ㄴWebDataBinder : 컨트롤러 요청될때 새롭게 만들어진다. 해당 컨트롤러에 메서드들을 호출하면 자동으로 검증기 적용(자세게 알필요 x) <br/>
ㄴ   dataBinder.addValidators(itemValidator);<br/>
ㄴ 사용 :  public String addItemV6(<strong>@Validated</strong> @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {..}

<br/>
#추가<br/>
errors.properties 추가 spring.messages.basename=messages,errors 등록<br/>
