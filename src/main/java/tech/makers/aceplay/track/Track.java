package tech.makers.aceplay.track;

import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2999s
@Entity
public class Track {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "user_id")
  private Integer user_id;

  private String title;

  private String artist;

  private URL publicUrl;

  public Track() { }

  public Track(String title, String artist, URL publicUrl, Integer user_id) {
    this.title = title;
    this.artist = artist;
    this.publicUrl = publicUrl;
    this.user_id = user_id;
  }

//  public Track(String title, String artist, String publicUrl) throws MalformedURLException {
//    this(title, artist, new URL(publicUrl));
//  }

  public String toString() {
    return String.format(
        "Track[id=%d title='%s' artist='%s' publicUrl='%s']", id, title, artist, publicUrl);
  }

  public int getUserId(){
    return this.user_id;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public URL getPublicUrl() {
    return publicUrl;
  }

  public void setPublicUrl(String publicUrl) throws MalformedURLException {
    this.publicUrl = new URL(publicUrl);
  }

  public void setPublicUrl(URL publicUrl) {
    this.publicUrl = publicUrl;
  }
}
