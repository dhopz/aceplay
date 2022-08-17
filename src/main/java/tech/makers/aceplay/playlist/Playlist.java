package tech.makers.aceplay.playlist;

import com.fasterxml.jackson.annotation.JsonGetter;
import tech.makers.aceplay.track.Track;
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


  @ManyToMany(fetch = FetchType.EAGER)
  @OrderBy("id ASC")
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
      throw new RuntimeException("Playlist must have a name");
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

  @Override
  public String toString() {
    return String.format("Playlist[id=%d name='%s']", id, name);
  }
}
