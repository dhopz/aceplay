package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.playlist.PlaylistsController;
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
  public Track create(@RequestBody TrackRequestModel trackRequestModel, @RequestHeader("authorization") String token) {
    if (trackRequestModel.getArtist() == null || trackRequestModel.getArtist().isEmpty() || trackRequestModel.getArtist().trim().isEmpty()) {
      System.out.println("Empty Artist");
      throw new EmptyArtistNameException("Empty Artist");
    } else {
      Track track = new Track(trackRequestModel.getTitle(), trackRequestModel.getArtist(), trackRequestModel.getPublicUrl());
      track.setUser(sessionService.findUser(token));
      return trackRepository.save(track);
    }
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public static class EmptyArtistNameException extends RuntimeException{
    public EmptyArtistNameException(String message) {
      super(message);
      System.out.println("Does it get here?");
    }
  }


  @PatchMapping("/api/tracks/{id}")
  public Track update(@PathVariable Long id, @RequestBody TrackRequestModel trackRequestModel) {
    Track track = trackRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + id));
    track.setTitle(trackRequestModel.getTitle());
    track.setArtist(trackRequestModel.getArtist());
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
