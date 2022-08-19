package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.makers.aceplay.EmptyFieldException;

@Service
public class PlaylistService {

    @Autowired PlaylistRepository playlistRepository;

    public Playlist addPlaylist(PlaylistRequestModel playlistRequestModel) {
        if(playlistRequestModel.getName() == null || playlistRequestModel.getName().isEmpty() || playlistRequestModel.getName().trim().isEmpty()) {
            throw new EmptyFieldException("Empty Playlist Name");
        } else {
            Playlist playlist = new Playlist(playlistRequestModel.getName(), playlistRequestModel.getTracks(), playlistRequestModel.getUser());
            return playlistRepository.save(playlist);
        }
    }
}
