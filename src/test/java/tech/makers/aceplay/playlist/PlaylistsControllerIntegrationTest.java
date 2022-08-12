package tech.makers.aceplay.playlist;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;
import tech.makers.aceplay.user.User;
import tech.makers.aceplay.user.UserRepository;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=1156s
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PlaylistsControllerIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Autowired private TrackRepository trackRepository;

  @Autowired private PlaylistRepository repository;

  @Autowired private UserRepository userRepository;

  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);


  @Test
  void WhenLoggedOut_PlaylistsIndexReturnsForbidden() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/playlists").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void WhenLoggedIn_AndThereAreNoPlaylists_PlaylistsIndexReturnsNoTracks() throws Exception {
    User kay = new User("kay", passwordEncoder.encode("pass"));
    userRepository.save(kay);
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

    mvc.perform(MockMvcRequestBuilders.get("/api/playlists")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void WhenLoggedIn_AndThereArePlaylists_PlaylistIndexReturnsTracks() throws Exception {
    User kay = new User("kay", passwordEncoder.encode("pass"));
    userRepository.save(kay);
    Track track = new Track("Title", "Artist", "https://example.org/");
    track.setUser(kay);
    trackRepository.save(track);
    Playlist p1 = new Playlist("My Playlist", Set.of(track));
    Playlist p2 = new Playlist("Their Playlist");
    p1.setUser(kay);
    p2.setUser(kay);
    repository.save(p1);
    repository.save(p2);
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

    mvc.perform(
            MockMvcRequestBuilders.get("/api/playlists").header("Authorization", token))
         .andExpect(status().isOk())
         .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
         .andExpect(jsonPath("$", hasSize(2)))
         .andExpect(jsonPath("$[0].name").value("My Playlist"))
         .andExpect(jsonPath("$[0].tracks[0].title").value("Title"))
         .andExpect(jsonPath("$[0].tracks[0].artist").value("Artist"))
         .andExpect(jsonPath("$[0].tracks[0].publicUrl").value("https://example.org/"))
         .andExpect(jsonPath("$[1].name").value("Their Playlist"));
  }

  @Test
  void WhenLoggedOut_PlaylistsGetReturnsForbidden() throws Exception {
    Playlist playlist = repository.save(new Playlist("My Playlist"));
    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/" + playlist.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndThereIsNoPlaylist_PlaylistsGetReturnsNotFound() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndThereIsAPlaylist_PlaylistGetReturnsPlaylist() throws Exception {
    Track track = trackRepository.save(new Track("Title", "Artist", "https://example.org/"));
    Playlist playlist = repository.save(new Playlist("My Playlist", Set.of(track)));

    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/" + playlist.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("My Playlist"))
        .andExpect(jsonPath("$.tracks[0].title").value("Title"))
        .andExpect(jsonPath("$.tracks[0].artist").value("Artist"))
        .andExpect(jsonPath("$.tracks[0].publicUrl").value("https://example.org/"));
  }

  @Test
  void WhenLoggedOut_PlaylistPostIsForbidden() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post("/api/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"My Playlist Name\"}"))
        .andExpect(status().isForbidden());
    assertEquals(0, repository.count());
  }

  @Test
  void WhenLoggedIn_PlaylistPostCreatesNewPlaylist() throws Exception {
    User kay = new User("kay", passwordEncoder.encode("pass"));
    userRepository.save(kay);
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
    mvc.perform(
            MockMvcRequestBuilders.post("/api/playlists").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"My Playlist Name\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("My Playlist Name"))
        .andExpect(jsonPath("$.tracks").value(IsEmptyCollection.empty()));

    Playlist playlist = repository.findFirstByOrderByIdAsc();
    assertEquals("My Playlist Name", playlist.getName());
    assertEquals(Set.of(), playlist.getTracks());
  }

  @Test
  void WhenLoggedOut_PlaylistAddTrackIsForbidden() throws Exception {
    Track track = trackRepository.save(new Track("Title", "Artist", "https://example.org/"));
    Playlist playlist = repository.save(new Playlist("My Playlist"));

    mvc.perform(
            MockMvcRequestBuilders.put("/api/playlists/" + playlist.getId() + "/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"" + track.getId() + "\"}"))
        .andExpect(status().isForbidden());

    Playlist currentPlaylist = repository.findById(playlist.getId()).orElseThrow();
    assertTrue(currentPlaylist.getTracks().isEmpty());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_TracksPostCreatesNewTrack() throws Exception {
    Track track = trackRepository.save(new Track("Title", "Artist", "https://example.org/"));
    Playlist playlist = repository.save(new Playlist("My Playlist"));

    mvc.perform(
            MockMvcRequestBuilders.put("/api/playlists/" + playlist.getId() + "/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"" + track.getId() + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Title"));

    Playlist updatedPlaylist = repository.findById(playlist.getId()).orElseThrow();

    assertEquals(1, updatedPlaylist.getTracks().size());
    Track includedTrack = updatedPlaylist.getTracks().stream().findFirst().orElseThrow();
    assertEquals(track.getId(), includedTrack.getId());
    assertEquals("Title", includedTrack.getTitle());
  }
}
