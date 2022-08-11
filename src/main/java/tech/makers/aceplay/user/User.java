package tech.makers.aceplay.user;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tech.makers.aceplay.playlist.Playlist;
import tech.makers.aceplay.track.Track;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=1156s
@Entity
@Table(
    name = "aceplay_user",
    uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String username;

  private String password;

  @OneToMany(fetch = FetchType.EAGER)
  private Set<Track> tracks;

  @OneToMany(fetch = FetchType.EAGER)
  private Set<Playlist> playlists;

  protected User() {}

  public User(String username, String password) { this(username, password, null, null); }

  public User(String username, String password, Set<Track> tracks, Set<Playlist> playlists) {
    this.username = username;
    this.password = password;
    this.tracks = tracks;
    this.playlists = playlists;
  }

  public Long getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @JsonGetter("tracks")
  public Set<Track> getTracks() {
    if (null == tracks) {
      return Set.of();
    }
    return tracks;
  }

  @JsonGetter("playlists")
  public Set<Playlist> getPlaylists() {
    if (null == playlists) {
      return Set.of();
    }
    return playlists;
  }

  @Override
  public String toString() {
    return String.format(
        "User[id=%d username='%s' password=HIDDEN]", id, username);
  }
}
