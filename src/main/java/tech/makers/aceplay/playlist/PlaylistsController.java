package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;





import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=0s
@RestController
public class PlaylistsController {
  public static final String NOPLAYLIST = "No playlist exists with id ";
  @Autowired private PlaylistRepository playlistRepository;

  @Autowired private TrackRepository trackRepository;

  @Autowired
  private SessionService sessionService;

  @Autowired
  private PlaylistService playlistService;


  @GetMapping("/api/playlists")
  public Iterable<Playlist> playlists() {
    return playlistService.playlists();
  }

  @PostMapping("/api/playlists")
  public Playlist createPlaylist(@RequestBody PlaylistRequestModel playlistRequestModel) {
    return playlistService.addPlaylist(playlistRequestModel);
  }

  @GetMapping("/api/playlists/{id}")
  public Playlist get(@PathVariable Long id) {
    return playlistService.findPlaylist(id);
  }
//
//  @PutMapping("/api/playlists/{id}/tracks")
//  public Track addTrack(@PathVariable Long id, @RequestBody TrackIdentifierDto trackIdentifierDto) {
//    Playlist playlist = playlistRepository.findById(id)
//            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
//    Track track = trackRepository.findById(trackIdentifierDto.getId())
//            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + trackIdentifierDto.getId()));
//    playlist.getTracks().add(track);
//    playlistRepository.save(playlist);
//    return track;
//  }
//
  @DeleteMapping("/api/playlists/{playlist_id}/tracks/{track_id}")
  public void delete(@PathVariable Long playlistId, @PathVariable Long trackId) {
    Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + playlistId));
    Track track = trackRepository.findById(trackId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + trackId));
    playlist.getTracks().remove(track);
    playlistRepository.save(playlist);
  }

  @DeleteMapping("/api/playlists/{id}")
  public void delete(@PathVariable Long id) {
    playlistService.deletePlaylist(id);
  }
}
