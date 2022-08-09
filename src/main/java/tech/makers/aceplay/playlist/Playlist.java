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

  private int userId;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Track> tracks;

  public Playlist() {}

  public Playlist(String name, int userId) {
    this(name, userId, null);
  }

  public Playlist(String name, int userId, Set<Track> tracks) {
    this.name = name;
    this.userId = userId;
    this.tracks = tracks;
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

  public int getUserId() {
    return this.userId;
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
    return String.format("Playlist[id=%d userId=%d name='%s']", id, userId, name);
  }
}
