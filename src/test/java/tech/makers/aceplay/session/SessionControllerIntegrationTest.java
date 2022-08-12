package tech.makers.aceplay.session;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;
import tech.makers.aceplay.user.User;
import tech.makers.aceplay.user.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=1853s
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SessionControllerIntegrationTest {
  @Autowired private MockMvc mvc;

  @Autowired private TrackRepository trackRepository;

  @Autowired private UserRepository repository;

  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

  @Test
  void withCorrectCredentials_SessionPostLogsUserIn() throws Exception {
    repository.save(new User("kay", passwordEncoder.encode("pass")));
    MvcResult result =
        mvc.perform(
                MockMvcRequestBuilders.post("/api/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\": \"kay\", \"password\": \"pass\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.user.username").value("kay"))
            .andReturn();

    String response = result.getResponse().getContentAsString();
    String token = JsonPath.parse(response).read("$.token");

    // Check if we can GET /api/session to prove we're logged in
    mvc.perform(MockMvcRequestBuilders.get("/api/session").header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user.username").value("kay"));
  }

  @Test
  void withIncorrectCredentials_SessionPostReturns403() throws Exception {
    repository.save(new User("kay", passwordEncoder.encode("pass")));
    mvc.perform(
            MockMvcRequestBuilders.post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"kay\", \"password\": \"wrong\"}"))
        .andExpect(status().isForbidden());
  }
}
