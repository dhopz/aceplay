package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.session.SessionService;

import java.util.Base64;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2410s
@RestController
public class TracksController {
  @Autowired private TrackRepository trackRepository;

  @Autowired
  private SessionService sessionService;

  @GetMapping("/api/tracks")
  public Iterable<Track> index(@RequestHeader("authorization") String token) {
    return trackRepository.findByUser(sessionService.findUser(token));
  }

  @PostMapping("/api/tracks")
  public Track create(@RequestBody Track track, @RequestHeader("authorization") String token) {
    track.setUser(sessionService.findUser(token));
    track.setArtist(track.checkArtistIsEmpty(track.getArtist()));
    track.setTitle(track.checkTitleIsEmpty(track.getTitle()));
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
