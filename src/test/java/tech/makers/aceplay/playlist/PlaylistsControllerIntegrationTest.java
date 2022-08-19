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


import java.util.Objects;
import java.net.URL;
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
  @WithMockUser
  void WhenLoggedIn_AndThereAreNoPlaylists_PlaylistsIndexReturnsNoTracks() throws Exception {

    mvc.perform(MockMvcRequestBuilders.get("/api/playlists")
                    .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndThereArePlaylists_PlaylistIndexReturnsPlaylists() throws Exception {
    Track track = trackRepository.save(new Track("Title", "Artist", new URL("https://example.org/")));
    repository.save(new Playlist("My Playlist", Set.of(track)));
    repository.save(new Playlist("Their Playlist"));

    mvc.perform(
            MockMvcRequestBuilders.get("/api/playlists"))
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
  @WithMockUser
  void WhenLoggedIn_AndThereArePlaylistsCreatedByADifferentUser_PlaylistsIndexReturnsNoTracks() throws Exception {
    User otherUser = userRepository.save(new User("Jim", "pass"));
    Track track = trackRepository.save(new Track("Title", "Artist", new URL("https://example.org/"), otherUser));
    repository.save(new Playlist("My Playlist", Set.of(track), otherUser));
    repository.save(new Playlist("Their Playlist", otherUser));
    mvc.perform(MockMvcRequestBuilders.get("/api/playlists")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
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
  @WithMockUser
  void WhenLoggedIn_PlaylistPostCreatesNewPlaylist() throws Exception {
    mvc.perform(
                    MockMvcRequestBuilders.post("/api/playlists")
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
  @WithMockUser
  void WhenLoggedIn_PlaylistPostCreatesNewPlaylistDefaultPlaylistName() throws Exception {
    mvc.perform(
                    MockMvcRequestBuilders.post("/api/playlists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertEquals("Empty Playlist Name", Objects.requireNonNull(result.getResolvedException()).getMessage()));
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

  @Test
  @WithMockUser
  void WhenLoggedIn_PlaylistDeleteDeletesPlaylist() throws Exception {
    Playlist playlist = repository.save(new Playlist("My Playlist"));

    mvc.perform(
                    MockMvcRequestBuilders.delete("/api/playlists/" + playlist.getId()))
            .andExpect(status().isOk());

    assertEquals(0, repository.count());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_ButNoPlaylist_PlaylistDeleteThrows404() throws Exception {
    mvc.perform(
                    MockMvcRequestBuilders.delete("/api/playlists/1"))
            .andExpect(status().isNotFound());
  }

  @Test
  void WhenLoggedOut_PlaylistDeleteIsForbidden() throws Exception {
    Playlist playlist = repository.save(new Playlist("My Playlist"));

    mvc.perform(
                    MockMvcRequestBuilders.delete("/api/playlists/" + playlist.getId()))
            .andExpect(status().isForbidden());

    assertEquals(1, repository.count());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_DeleteTrackFromPlaylist() throws Exception {
    Playlist playlist = repository.save(new Playlist("My Playlist"));
    Track track = trackRepository.save(new Track("Title", "Artist", "https://example.org/"));

    mvc.perform(
                    MockMvcRequestBuilders.put("/api/playlists/" + playlist.getId() + "/tracks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\": \"" + track.getId() + "\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Title"));

    mvc.perform(
            MockMvcRequestBuilders.delete("/api/playlists/" + playlist.getId() + "/tracks/" + track.getId()))

            .andExpect(status().isOk());

    Playlist updatedPlaylist = repository.findById(playlist.getId()).orElseThrow();


    assertEquals(0, updatedPlaylist.getTracks().size());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_ButNoTracksInPlaylist_DeleteTrackOnPlaylistThrows404() throws Exception {
    Playlist playlist = repository.save(new Playlist("My Playlist"));
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/playlists/" + playlist.getId() + "/tracks/1"))
            .andExpect(status().isNotFound());
  }

  @Test
  void WhenLoggedOut_DeleteTrackFromPlaylistIsForbidden() throws Exception {
    Track track = trackRepository.save(new Track("Title", "Artist", "https://example.org/"));
    Playlist playlist = repository.save(new Playlist("My Playlist", Set.of(track)));

    mvc.perform(
            MockMvcRequestBuilders.delete("/api/playlists/" + playlist.getId() + "/tracks/" + track.getId()))
            .andExpect(status().isForbidden());

    Playlist updatedPlaylist = repository.findById(playlist.getId()).orElseThrow();

    assertEquals(1, updatedPlaylist.getTracks().size());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndThereAreTracksNotCreatedByUserAndTheyAreSavedToAPlaylist_PopularTracksReturnsTracks() throws Exception {
    User mockInDataBase = userRepository.save(new User("user", "pass"));
    User jim = userRepository.save(new User("Jim", "pass"));
    Track blueLineSwingerJim = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    repository.save(new Playlist("Playlist Jim", Set.of(blueLineSwingerJim), jim));

    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/populartracks")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("Blue Line Swinger"))
            .andExpect(jsonPath("$[0].artist").value("Yo La Tengo"));
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndThereAreNoTracksAddedToOtherUsersPlaylists_PopularTracksReturnsNoTracks() throws Exception {
    User signedInUser = userRepository.save(new User("user", "pass"));
    User jim = userRepository.save(new User("Jim", "pass"));
    repository.save(new Playlist("Playlist Jim", jim));

    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/populartracks")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndOnlyTrackAddedIsToUsersOwnPlaylist_PopularTracksReturnsNoTracks() throws Exception {
    User signedInUser = userRepository.save(new User("user", "pass"));
    Track blueLineSwinger = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), signedInUser));
    repository.save(new Playlist("Playlist Jim", Set.of(blueLineSwinger), signedInUser));

    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/populartracks")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_SeeListOfTracksAddedToTheMostPlaylists() throws Exception {
    User signedInUser = userRepository.save(new User("user", "pass"));
    User jim = userRepository.save(new User("Jim", "pass"));
    User jane = userRepository.save(new User("jane", "pass"));
    User jemima = userRepository.save(new User("jemima", "pass"));

    Track blueLineSwingerJim = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track blueLineSwingerJane = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), jane));
    Track blueLineSwingerJemima = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), jemima));
    Track backstreetsBackJane = trackRepository.save(new Track("Backstreet's Back", "Backstreet Boys", new URL("http://example.org/track.mp3"), jane));
    Track backstreetsBackJim = trackRepository.save(new Track("Backstreet's Back", "Backstreet Boys", new URL("http://example.org/track.mp3"), jim));
    Track twistAndShoutJim = trackRepository.save(new Track("Twist and Shout", "The Beatles", new URL("http://example.org/track.mp3"), jim));

    repository.save(new Playlist("Playlist Jim", Set.of(blueLineSwingerJim, backstreetsBackJim, twistAndShoutJim), jim));
    repository.save(new Playlist("Playlist Jane", Set.of(blueLineSwingerJane, backstreetsBackJane), jane));
    repository.save(new Playlist("Playlist Jemima", Set.of(blueLineSwingerJemima), jemima));


    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/populartracks")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].title").value("Blue Line Swinger"))
            .andExpect(jsonPath("$[0].artist").value("Yo La Tengo"))
            .andExpect(jsonPath("$[1].title").value("Backstreet's Back"))
            .andExpect(jsonPath("$[1].artist").value("Backstreet Boys"))
            .andExpect(jsonPath("$[2].title").value("Twist and Shout"))
            .andExpect(jsonPath("$[2].artist").value("The Beatles"));
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_OnlyDuplicatesOfTracksFromOwnLibraryInOtherPlaylists_PopularTracksReturnsNoTracks() throws Exception {
    User signedInUser = userRepository.save(new User("user", "pass"));
    User jim = userRepository.save(new User("Jim", "pass"));
    Track blueLineSwingerJim = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track blueLineSwingerSignedInUser = trackRepository.save(new Track("Blue Line Swinger", "Yo La Tengo", new URL("http://example.org/track.mp3"), signedInUser));
    repository.save(new Playlist("Playlist Jim", Set.of(blueLineSwingerJim), jim));


    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/populartracks")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndMoreThanTenTracksInOtherUsersPlaylists_PopularTracksReturnsTenTracks() throws Exception {
    User signedInUser = userRepository.save(new User("user", "pass"));
    User jim = userRepository.save(new User("Jim", "pass"));
    Track a = trackRepository.save(new Track("A", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track b = trackRepository.save(new Track("B", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track c = trackRepository.save(new Track("C", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track d = trackRepository.save(new Track("D", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track e = trackRepository.save(new Track("E", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track f = trackRepository.save(new Track("F", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track g = trackRepository.save(new Track("G", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track h = trackRepository.save(new Track("H", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track i = trackRepository.save(new Track("I", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track j = trackRepository.save(new Track("J", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track k = trackRepository.save(new Track("K", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track l = trackRepository.save(new Track("L", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track m = trackRepository.save(new Track("M", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track n = trackRepository.save(new Track("N", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track o = trackRepository.save(new Track("O", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track p = trackRepository.save(new Track("P", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));
    Track q = trackRepository.save(new Track("Q", "Yo La Tengo", new URL("http://example.org/track.mp3"), jim));

    repository.save(new Playlist("Playlist Jim", Set.of(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q), jim));


    mvc.perform(MockMvcRequestBuilders.get("/api/playlists/populartracks")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(10)));
  }

}
