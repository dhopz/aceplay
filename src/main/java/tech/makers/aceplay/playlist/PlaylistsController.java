package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.makers.aceplay.track.Track;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=0s
@RestController
public class PlaylistsController {
  public static final String NOPLAYLIST = "No playlist exists with id ";

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

  @PutMapping("/api/playlists/{id}/tracks")
  public Track addTrack(@PathVariable Long id, @RequestBody TrackIdentifierDto trackIdentifierDto) {
    return playlistService.addTrack(id, trackIdentifierDto);
  }

  @DeleteMapping("/api/playlists/{playlist_id}/tracks/{track_id}")
  public void deleteTracks(@PathVariable Long playlistId, @PathVariable Long trackId) {
    playlistService.deleteTracks(playlistId, trackId);
  }

  @DeleteMapping("/api/playlists/{id}")
  public void deletePlaylist(@PathVariable Long id) {
    playlistService.deletePlaylist(id);
  }
}
