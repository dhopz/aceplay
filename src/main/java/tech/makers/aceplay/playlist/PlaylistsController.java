package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;


import java.util.*;
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
    Iterable<Track> ownTracks = trackRepository.findByUser(sessionService.findUser());
    ArrayList<String> ownTrackDetails = new ArrayList<>();

    for(Playlist playlist : allPlaylists){
      if(!playlist.getUser().getId().equals(sessionUserId)){
        for(Track track : playlist.getTracks())
          playlistTracks.add(track);
      }
    }

    for (Track track : ownTracks) {
      String trackDetails = track.getTitle() + " by " + track.getArtist();
      ownTrackDetails.add(trackDetails);
    }

    HashMap<String, Long> trackPopularity = new HashMap<String, Long>();

    for (Track track : playlistTracks) {
      String trackDetails = track.getTitle() + " by " + track.getArtist();
      if(!ownTrackDetails.contains(trackDetails)) {
        trackPopularity.put(trackDetails, trackPopularity.containsKey(trackDetails) ? trackPopularity.get(trackDetails) + 1 : 1);
      }
    }

    ArrayList<Map.Entry<String,Integer>> popularityOfNewTracks = new ArrayList(trackPopularity.entrySet());
    Collections.sort(popularityOfNewTracks, Collections.reverseOrder(Comparator.comparing(Map.Entry::getValue)));

    int maxSize = popularityOfNewTracks.size() > 10 ? 10 : popularityOfNewTracks.size();

    ArrayList<Track> tracksToReturn = new ArrayList<>();
    for (Map.Entry<String,Integer> entry: popularityOfNewTracks) {
      String[] details = entry.getKey().split(" by ");
      tracksToReturn.add(new Track(details[0], details[1]));
    }

    List<Track> top10 = tracksToReturn
            .stream()
            .limit(maxSize)
            .collect(Collectors.toList());
    return top10;
  }

}
