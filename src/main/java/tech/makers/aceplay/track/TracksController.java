package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2410s
@RestController
public class TracksController {
  @Autowired private TrackService trackService;

  @GetMapping("/api/tracks")
  public Iterable<Track> index() {
    return trackService.allTracks();
  }

  @PostMapping("/api/tracks")
  public Track create(@RequestBody TrackRequestModel trackRequestModel) {
    return trackService.createTrack(trackRequestModel);
  }

  @PatchMapping("/api/tracks/{id}")
  public Track update(@PathVariable Long id, @RequestBody TrackRequestModel trackRequestModel) {
    return trackService.updateTrack(id,trackRequestModel);
  }

  @DeleteMapping("/api/tracks/{id}")
  public void delete(@PathVariable Long id) {
    trackService.deleteTrack(id);
  }
}
