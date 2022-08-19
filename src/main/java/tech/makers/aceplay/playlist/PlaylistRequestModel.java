package tech.makers.aceplay.playlist;

import tech.makers.aceplay.track.Track;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class PlaylistRequestModel {
    @NotEmpty(message = "Name may not be empty")
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
