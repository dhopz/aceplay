package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;


import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2410s
@RestController
public class TracksController {
  @Autowired private TrackRepository trackRepository;

  @Autowired private TrackService trackService;
  @Autowired
  private SessionService sessionService;

  @GetMapping("/api/tracks")
  public Iterable<Track> index() {
    return trackRepository.findByUser(sessionService.findUser());
  }

  @PostMapping("/api/tracks")
  public Track create(@RequestBody TrackRequestModel trackRequestModel) {
    return trackService.createTrack(trackRequestModel);
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
    trackService.deleteTrack(id);
  }
}
