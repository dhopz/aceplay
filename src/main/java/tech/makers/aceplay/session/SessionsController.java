package tech.makers.aceplay.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.makers.aceplay.user.User;
import tech.makers.aceplay.user.UserRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=1554s
@RestController
public class SessionsController {
  @Autowired private UserRepository userRepository;

  @Autowired private SessionService sessionService;

  @PostMapping("/api/session")
  public Session create(@RequestBody @Valid SessionsController.LoginForm login) {
    User user = userRepository.findByUsername(login.username);
    String token = sessionService.login(login.username, login.password);
    return new Session(user, token);
  }

  @GetMapping("/api/session")
  public Session get(Principal principal, @RequestHeader("authorization") String token) {
    User user = userRepository.findByUsername(principal.getName());
    return new Session(user, token);
  }

  static class LoginForm {
    @NotNull String username;

    @NotNull String password;

    public LoginForm(String username, String password) {
      this.username = username;
      this.password = password;
    }
  }
}
