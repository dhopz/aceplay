package tech.makers.aceplay.playlist;

import tech.makers.aceplay.track.Track;

import java.util.Set;

public class PlaylistRequestModel {
    private String name;
    private Set<Track> tracks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }


}
