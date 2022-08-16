package tech.makers.aceplay.track;

import org.junit.jupiter.api.Test;
import tech.makers.aceplay.playlist.PlaylistRequestModel;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrackRequestModelTest {
    @Test
    void testConstructs() throws MalformedURLException {
        TrackRequestModel trackRequestModel = new TrackRequestModel();
        trackRequestModel.setTitle("New Title");
        trackRequestModel.setArtist("New Artist");
        URL url = new URL("https","example.com","track.mp3");
        trackRequestModel.setPublicURL(url);
        assertEquals("New Title", trackRequestModel.getTitle());
        assertEquals("New Artist", trackRequestModel.getArtist());
        assertEquals(url,trackRequestModel.getPublicUrl());
    }
}
