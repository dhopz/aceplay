package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {

    @Autowired PlaylistRepository playlistRepository;

    public Playlist addPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }
}
