package tech.makers.aceplay.playlist;

import com.fasterxml.jackson.annotation.JsonGetter;
import tech.makers.aceplay.track.Track;

import javax.persistence.*;
import java.util.Set;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=448s
@Entity
public class Playlist {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Track> tracks;

  public Playlist() {}

//  public Playlist(String name) {
//    this(name, null);
//  }

  public Playlist(String name) {
    if (name == null || name.isEmpty() || name.trim().isEmpty()) {
      this.name = "PlayList A";
    } else{
      this.name = name;
    }
    this.tracks = null;
  }

  public Playlist(String name, Set<Track> tracks) {
    if (name == null || name.isEmpty() || name.trim().isEmpty()) {
      this.name = "PlayList A";
    } else{
      this.name = name;
    }
    this.tracks = tracks;
  }

  public String checkIfNameIsEmpty(String name) {
    System.out.println("Do I get here?.");
    if (name == null || name.isEmpty() || name.trim().isEmpty()) {
      System.out.println("String is null, empty or blank.");
      return "Newbie Playlist";
    } else {
      System.out.println("String is neither null, empty nor blank");
      return name;
    }
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
