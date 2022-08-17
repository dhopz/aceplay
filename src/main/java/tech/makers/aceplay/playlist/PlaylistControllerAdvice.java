package tech.makers.aceplay.playlist;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class PlaylistControllerAdvice {
    @ExceptionHandler(PlaylistsController.EmptyPlaylistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(PlaylistsController.EmptyPlaylistException exception) {
        return String.format("Needs a Playlist Name %s %n",exception.getMessage()) ;
    }
}
