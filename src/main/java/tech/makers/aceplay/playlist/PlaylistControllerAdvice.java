//package tech.makers.aceplay.playlist;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//public class PlaylistControllerAdvice {
//    @ExceptionHandler(PlaylistsController.EmptyPlaylistException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String handleException(PlaylistsController.EmptyPlaylistException exception) {
//        System.out.println("Do I get here?");
//        return String.format("The HTTP Status will be Internal Server Error (CODE 500)%n %s%n",exception.getMessage()) ;
//    }
//}
