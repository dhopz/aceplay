package tech.makers.aceplay.track;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class TracksControllerAdvice {
    @ExceptionHandler(TracksController.EmptyArtistNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(TracksController.EmptyArtistNameException exception) {
        return String.format("Needs an Artist Name %s %n",exception.getMessage()) ;
    }
}
