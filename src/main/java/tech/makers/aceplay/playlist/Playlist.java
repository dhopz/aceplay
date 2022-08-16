package tech.makers.aceplay.playlist;

import com.fasterxml.jackson.annotation.JsonGetter;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.trackIdComparator;
import tech.makers.aceplay.user.User;

import javax.persistence.*;
import java.util.*;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=448s
@Entity
public class Playlist {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;


  @OneToMany(fetch = FetchType.EAGER)
  @OrderBy("id ASC")
  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Track> tracks;

  @ManyToOne(fetch = FetchType.EAGER)
  private User user;

  public Playlist() {}

  public Playlist(String name) {
    this(name, null);
  }

  public Playlist(String name, Set<Track> tracks) {
    this.name = name;
    this.tracks = tracks;
  }

  public String checkIfNameIsEmpty(String name) {
    if (name == null || name.isEmpty() || name.trim().isEmpty()) {
      System.out.println("String is null, empty or blank.");
//      return "Newbie Playlist";
      return randomPlaylistNameGenerator();
    } else {
      return name;
    }
  }

  public String randomPlaylistNameGenerator(){
    String[] newNames = {"Cool","Random","Newbie","Awesome","MegaMix"};
    Random r = new Random();
    int randomNumber=r.nextInt(newNames.length);
    return newNames[randomNumber] + " Playlist";

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }


  @JsonGetter("tracks")
  public Set<Track> getTracks() {
    if (null == tracks) {
      return Set.of();
    }
    return tracks;
  }

  public Set<Track> orderedTracks(){
    Set<Track> allTracks = new TreeSet<>(new trackIdComparator());
    allTracks.addAll(tracks);

    for (Track track: allTracks){
      System.out.println(track.getId());
      System.out.println("this is what I came for");
    }

    return allTracks;
  }

  @Override
  public String toString() {
    return String.format("Playlist[id=%d name='%s']", id, name);
  }
}
