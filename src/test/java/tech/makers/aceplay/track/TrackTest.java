package tech.makers.aceplay.track;

import org.junit.jupiter.api.Test;
import tech.makers.aceplay.track.Track;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=798s
class TrackTest {
//  @Test
//  void testConstructs() throws MalformedURLException {
//    Track subject = new Track("Hello, world!", "Sarah", "https://example.org/track.mp3");
//    assertEquals("Hello, world!", subject.getTitle());
//    assertEquals("Sarah", subject.getArtist());
//    assertEquals("https://example.org/track.mp3", subject.getPublicUrl().toString());
//    assertEquals(null, subject.getId());
//  }

  @Test
  void testConstructs() throws MalformedURLException {
    Track subject = new Track("Hello, world!", "Sarah", "1", "https://example.org/track.mp3");
    assertEquals("Hello, world!", subject.getTitle());
    assertEquals("Sarah", subject.getArtist());
    assertEquals("https://example.org/track.mp3", subject.getPublicUrl().toString());
    assertEquals(null, subject.getId());
    assertEquals("1", subject.getUserId());
  }

  @Test
  void testToString() throws MalformedURLException {
    Track subject = new Track("Hello, world!", "Sarah", "1", "https://example.org/track.mp3");
    assertEquals(
        "Track[id=null title='Hello, world!' artist='Sarah' publicUrl='https://example.org/track.mp3']",
        subject.toString());
  }

  @Test
  void testSetPublicUrl() throws MalformedURLException {
    Track subject = new Track();
    subject.setPublicUrl("https://example.org/track.mp3");
    assertEquals("https://example.org/track.mp3", subject.getPublicUrl().toString());
  }
}
