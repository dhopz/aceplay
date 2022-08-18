package tech.makers.aceplay.playlist;

import java.util.Collection;

public interface PlaylistService {
    public abstract void createPlaylist(Playlist playlist);
    public abstract void updatePlaylist(String id, Playlist playlist);
    public abstract void deletePlaylist(String id);
    public abstract Collection<Playlist> getPlaylists();
}
