package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.user.User;
import tech.makers.aceplay.user.UserRepository;

import java.util.Base64;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2410s
@RestController
public class TracksController {
  @Autowired private TrackRepository trackRepository;

  @Autowired private UserRepository userRepository;

  @GetMapping("/api/tracks")
  public Iterable<Track> index() {
    return trackRepository.findAll();
  }

  @PostMapping("/api/tracks")
  public Track create(@RequestBody Track track, @RequestHeader("authorization") String token) {
    String[] chunks = token.split("\\.");
    Base64.Decoder decoder = Base64.getUrlDecoder();
    String username = new String(decoder.decode(chunks[1])).split("\"")[3];
    User user = userRepository.findByUsername(username);
    track.setUser(user);
    return trackRepository.save(track);
  }

  @PatchMapping("/api/tracks/{id}")
  public Track update(@PathVariable Long id, @RequestBody Track newTrack) {
    Track track = trackRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + id));
    track.setTitle(newTrack.getTitle());
    track.setArtist(newTrack.getArtist());
    trackRepository.save(track);
    return track;
  }

  @DeleteMapping("/api/tracks/{id}")
  public void delete(@PathVariable Long id) {
    Track track = trackRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + id));
    trackRepository.delete(track);
  }
}
