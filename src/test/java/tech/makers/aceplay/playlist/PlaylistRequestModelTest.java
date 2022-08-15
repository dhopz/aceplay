package tech.makers.aceplay.playlist;

import org.junit.jupiter.api.Test;
import tech.makers.aceplay.track.Track;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlaylistRequestModelTest {

        @Test
        void testConstructsWithTracks() throws MalformedURLException {
            Track track = new Track("Title", "Artist", "https://example.org/");
            PlaylistRequestModel playlistRequestModel = new PlaylistRequestModel();
            playlistRequestModel.setName("New Playlist");
            Set<Track> newTracks = new HashSet<Track>();
            newTracks.add(track);
            playlistRequestModel.setTracks(newTracks);
            assertEquals("New Playlist", playlistRequestModel.getName());
            assertEquals(newTracks,playlistRequestModel.getTracks());
        }

        @Test
        void testConstructsWithOutTracks() throws MalformedURLException {
            PlaylistRequestModel playlistRequestModel = new PlaylistRequestModel();
            playlistRequestModel.setName("New Playlist");
            assertEquals("New Playlist", playlistRequestModel.getName());
            assertNull(playlistRequestModel.getTracks());

    }
    }

