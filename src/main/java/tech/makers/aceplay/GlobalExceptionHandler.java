//package tech.makers.aceplay;
//
//import org.hibernate.TypeMismatchException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@ControllerAdvice
//class GlobalExceptionHandler {
//    @ExceptionHandler(TypeMismatchException::class)
//    fun handleTypeMismatchException(e: TypeMismatchException): HttpStatus {
//        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value '${e.value}'", e)
//    }
//
//    @ExceptionHandler(WebExchangeBindException::class)
//    fun handleWebExchangeBindException(e: WebExchangeBindException): HttpStatus {
//        throw object : WebExchangeBindException(e.methodParameter!!, e.bindingResult) {
//            override val message = "${fieldError?.field} has invalid value '${fieldError?.rejectedValue}'"
//        }
//    }
//}
