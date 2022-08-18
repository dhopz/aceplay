package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class PlaylistServiceImpl implements PlaylistService{
    @Autowired
    private PlaylistRepository playlistRepository;

    public void createPlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }

    @Override
    public void updatePlaylist(Long id, Playlist playlist) {

    }

    @Override
    public void deletePlaylist(Long id) {

    }

    @Override
    public Collection<Playlist> getPlaylists() {
        return null;
    }
}
