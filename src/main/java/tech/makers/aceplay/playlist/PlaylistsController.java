package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
  public Iterable<Playlist> playlists() {
    return playlistRepository.findByUser(sessionService.findUser());
  }

  @PostMapping("/api/playlists")
  public Playlist create(@RequestBody PlaylistRequestModel playlistRequestModel) {
    if(playlistRequestModel.getName() == null || playlistRequestModel.getName().isEmpty() || playlistRequestModel.getName().trim().isEmpty()){
      throw new EmptyFieldException("Empty Playlist Name");
    } else {
      Playlist playlist = new Playlist(playlistRequestModel.getName(), playlistRequestModel.getTracks());
      playlist.setUser(sessionService.findUser());
      return playlistRepository.save(playlist);
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
    playlistRepository.save(playlist);
    return track;
  }

  @DeleteMapping("/api/playlists/{playlist_id}/tracks/{track_id}")
  public void delete(@PathVariable Long playlist_id, @PathVariable Long track_id) {
    Playlist playlist = playlistRepository.findById(playlist_id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + playlist_id));
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

  @GetMapping("/api/playlists/populartracks")
  public Iterable<Track> popularTracks() {
    Iterable<Playlist> allPlaylists = playlistRepository.findAll();
    ArrayList<Track> playlistTracks = new ArrayList<>();
    Long sessionUserId = sessionService.findUser().getId();
    ArrayList<Track> ownTracks = new ArrayList<>();
    for (Track track : trackRepository.findByUser(sessionService.findUser())) { ownTracks.add(track);}

    for(Playlist playlist : allPlaylists){
      if(!playlist.getUser().getId().equals(sessionUserId)){
        for(Track track : playlist.getTracks()) {
          if (ownTracks.stream().noneMatch(ownTrack -> ownTrack.getTitle() == track.getTitle() && ownTrack.getArtist() == track.getArtist())) {
            playlistTracks.add(track);
          }
        }
      }
    }

    HashMap<String, Long> trackPopularity = new HashMap<>();
    for (Track track : playlistTracks) {
      String trackDetails = track.getTitle() + "\n" + track.getArtist();
      trackPopularity.put(trackDetails, trackPopularity.containsKey(trackDetails) ? trackPopularity.get(trackDetails) + 1 : 1);
    }
    ArrayList<Map.Entry<String,Integer>> popularityOfNewTracks = new ArrayList(trackPopularity.entrySet());
    popularityOfNewTracks.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

    ArrayList<Track> tracksToReturn = new ArrayList<>();
    for (Map.Entry<String,Integer> entry: popularityOfNewTracks) {
      tracksToReturn.add(new Track(entry.getKey().split("\n")[0], entry.getKey().split("\n")[1]));
    }
    return tracksToReturn.stream().limit(Math.min(popularityOfNewTracks.size(), 10)).collect(Collectors.toList());
  }

}
