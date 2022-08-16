package tech.makers.aceplay.track;

import java.net.URL;

public class TrackRequestModel {
    private String artist;
    private String title;
    private URL publicUrl;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getPublicUrl() {
        return publicUrl;
    }

    public void setPublicURL(URL publicURL) {
        this.publicUrl = publicURL;
    }
}
