package tech.makers.aceplay.playlist;

import org.junit.jupiter.api.Test;
import tech.makers.aceplay.playlist.Playlist;

import java.net.MalformedURLException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=1099s
class PlaylistTest {
  @Test
  void testConstructs() {
    Playlist subject = new Playlist("Hello, world!", 1, Set.of());
    assertEquals("Hello, world!", subject.getName());
    assertEquals(1, subject.getUserId());
    assertEquals(Set.of(), subject.getTracks());
    assertEquals(null, subject.getId());
  }

  @Test
  void testToString() {
    Playlist subject = new Playlist("Hello, world!", 1);
    assertEquals(
        "Playlist[id=null userId=1 name='Hello, world!']",
        subject.toString());
  }
}

