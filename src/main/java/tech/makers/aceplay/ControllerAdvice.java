package tech.makers.aceplay;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@org.springframework.web.bind.annotation.ControllerAdvice
@ResponseBody
public class ControllerAdvice {
    @ExceptionHandler(EmptyFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(EmptyFieldException exception) {
        return String.format("Empty Field Error %s %n",exception.getMessage()) ;
    }
}
