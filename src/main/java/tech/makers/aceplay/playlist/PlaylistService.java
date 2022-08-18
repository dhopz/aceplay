package tech.makers.aceplay.playlist;

import java.util.Collection;

public interface PlaylistService {


    static void createPlaylist(Playlist playlist) {
    }



    public abstract void updatePlaylist(Long id, Playlist playlist);
    public abstract void deletePlaylist(Long id);
    public abstract Collection<Playlist> getPlaylists();
}
