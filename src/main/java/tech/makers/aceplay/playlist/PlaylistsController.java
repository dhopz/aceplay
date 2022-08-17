package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
//import tech.makers.aceplay.NoSuchElementFoundException;
import tech.makers.aceplay.session.SessionService;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=0s
@RestController
public class PlaylistsController {
  public static final String NOPLAYLIST = "No playlist exists with id ";
  @Autowired private PlaylistRepository playlistRepository;

  @Autowired private TrackRepository trackRepository;

  @Autowired
  private SessionService sessionService;

  @GetMapping("/api/playlists")

  public Iterable<Playlist> playlists(@RequestHeader("authorization") String token) {
    return playlistRepository.findByUser(sessionService.findUser(token));
  }

  @PostMapping("/api/playlists")
  public Playlist create(@RequestBody PlaylistRequestModel playlistRequestModel, @RequestHeader("authorization") String token) {
    if(playlistRequestModel.getName() == null || playlistRequestModel.getName().isEmpty() || playlistRequestModel.getName().trim().isEmpty()){
      throw new EmptyPlaylistException("User hasn't provided Playlist Name parameter");
    } else {
      Playlist playlist = new Playlist(playlistRequestModel.getName(), playlistRequestModel.getTracks());
      playlist.setUser(sessionService.findUser(token));
      return playlistRepository.save(playlist);
    }
  }
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public static class EmptyPlaylistException extends RuntimeException{
    public EmptyPlaylistException(String message) {
      super(message);
    }
  }


  @GetMapping("/api/playlists/{id}")
  public Playlist get(@PathVariable Long id) {
    return playlistRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
  }

  @PutMapping("/api/playlists/{id}/tracks")
  public Track addTrack(@PathVariable Long id, @RequestBody TrackIdentifierDto trackIdentifierDto) {
    Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
    Track track = trackRepository.findById(trackIdentifierDto.getId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + trackIdentifierDto.getId()));
    playlist.getTracks().add(track);
//    playlist.orderedTracks(track);
    playlistRepository.save(playlist);
    return track;
  }

  @DeleteMapping("/api/playlists/{playlist_id}/tracks/{track_id}")
  public void delete(@PathVariable Long playlist_id, @PathVariable Long track_id) {
    Playlist playlist = playlistRepository.findById(playlist_id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No playlist exists with id " + playlist_id));
    Track track = trackRepository.findById(track_id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + track_id));
    playlist.getTracks().remove(track);
    playlistRepository.save(playlist);
  }

  @DeleteMapping("/api/playlists/{id}")
  public void delete(@PathVariable Long id) {
    Playlist playlist = playlistRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
    playlistRepository.delete(playlist);
  }
}
