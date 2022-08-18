package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;


import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2410s
@RestController
public class TracksController {
  @Autowired private TrackRepository trackRepository;


  @Autowired
  private SessionService sessionService;

  @GetMapping("/api/tracks")
  public Iterable<Track> index() {
    return trackRepository.findByUser(sessionService.findUser());
  }

  @PostMapping("/api/tracks")
  public Track create(@RequestBody TrackRequestModel trackRequestModel) {
    if (trackRequestModel.getArtist() == null || trackRequestModel.getArtist().isEmpty() || trackRequestModel.getArtist().trim().isEmpty()) {
      throw new EmptyFieldException("Empty Artist");
    }else{
      if (trackRequestModel.getTitle() == null || trackRequestModel.getTitle().isEmpty() || trackRequestModel.getTitle().trim().isEmpty()) {
        throw new EmptyFieldException("Empty Title");
      }else {
        Track track = new Track(trackRequestModel.getTitle(), trackRequestModel.getArtist(), trackRequestModel.getPublicUrl());
        track.setUser(sessionService.findUser());
        return trackRepository.save(track);
      }
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

  @GetMapping("/api/tracks/suggestions")
  public Iterable<Track> suggestedTracks() {
    Iterable<Track> allTracks = trackRepository.findAll();
    ArrayList<Track> tracksToReturn = new ArrayList<Track>();

    Long sessionUserId = sessionService.findUser().getId();

    for(Track track : allTracks){
      if(!track.getUser().getId().equals(sessionUserId)){
        tracksToReturn.add(track);
      }
    }
//   Iterable<Track> returnValue = tracksToReturn;
    return tracksToReturn;
  }
}
