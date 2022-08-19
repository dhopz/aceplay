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
  public Iterable<String> popularTracks() {
    Iterable<Playlist> allPlaylists = playlistRepository.findAll();
    ArrayList<Track> playlistTracks = new ArrayList<>();
    Long sessionUserId = sessionService.findUser().getId();
    Iterable<Track> ownTracks = trackRepository.findByUser(sessionService.findUser());
    ArrayList<String> ownTrackDetails = new ArrayList<>();

    for(Playlist playlist : allPlaylists){
      if(!playlist.getUser().getId().equals(sessionUserId)){
        for(Track track : playlist.getTracks())
          playlistTracks.add(track);
        System.out.println(playlistTracks);
      }
    }

    for (Track track : ownTracks) {
      String trackDetails = track.getTitle() + " by " + track.getArtist();
      ownTrackDetails.add(trackDetails);
    }

//    Track a = new Track ("Title", "Artist");
//    Track b = new Track ("Title", "Artist");
//    String c = new String("abc");
//    String d = new String("abc");
//
//      System.out.println(a);
//      System.out.println(b);
//
//      if(a.toString() == b.toString()) {
//        System.out.println("True true true");
//      }
    HashMap<String, Long> trackPopularity = new HashMap<String, Long>();

    for (Track track : playlistTracks) {
      String trackDetails = new String(track.getTitle() + " by " + track.getArtist());
//      Track hashKeyTrack = new Track(track.getTitle(), track.getArtist());
      System.out.println(trackDetails);
      if(!ownTrackDetails.contains(trackDetails)) {
        trackPopularity.put(trackDetails, trackPopularity.containsKey(trackDetails) ? trackPopularity.get(trackDetails) + 1 : 1);
      }
    }

    ArrayList<Long> ranking = new ArrayList<Long>();
    for (Long value : trackPopularity.values()) {
      ranking.add(value);
      System.out.println(value);
    }
    System.out.println("\n\n");
    Collections.sort(ranking, Collections.reverseOrder());

    ArrayList<String> details = new ArrayList<String>();
    for(int i = 0; i < ranking.size() -1;) {
      for (HashMap.Entry<String, Long> entry : trackPopularity.entrySet()) {
        System.out.println(entry.getValue());
        System.out.println(entry.getKey());

        System.out.println(ranking.get(i));
        if (entry.getValue() == ranking.get(i)) {
          details.add(entry.getKey());
          i++;
        }
      }
    }

    System.out.println(details);

    int maxSize = ranking.size();

    if (ranking.size() > 10) {
      maxSize = 10;
    }

    List<String> top10 = details
            .stream()
            .limit(maxSize)
            .collect(Collectors.toList());
    return top10;
  }

}
