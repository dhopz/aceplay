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

  @Column(columnDefinition = "BOOLEAN DEFAULT false")
  private boolean cool;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Track> tracks;


  public Playlist() {}

  public Playlist(String name, Boolean cool) {
    this(name, null, cool);
  }

  public Playlist(String name, Set<Track> tracks, Boolean cool) {
    this.name = name;
    this.tracks = tracks;
    this.cool = cool;
  }



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getCool(){
    return cool;
  }

  public void setCool(Boolean cool){
    this.cool = cool;
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
    return String.format("Playlist[id=%d name='%s' cool='%s']", id, name, cool);
  }
}
