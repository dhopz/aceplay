package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;

import java.util.Collection;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PlaylistService {
    public static final String NOPLAYLIST = "No playlist exists with id ";

    @Autowired PlaylistRepository playlistRepository;

    @Autowired SessionService sessionService;

    public Playlist addPlaylist(PlaylistRequestModel playlistRequestModel) {
        if(playlistRequestModel.getName() == null || playlistRequestModel.getName().isEmpty() || playlistRequestModel.getName().trim().isEmpty()) {
            throw new EmptyFieldException("Empty Playlist Name");
        } else {
            playlistRequestModel.setUser(sessionService.findUser());
            Playlist playlist = new Playlist(playlistRequestModel.getName(), playlistRequestModel.getTracks(), playlistRequestModel.getUser());
            return playlistRepository.save(playlist);
        }
    }

    public void deletePlaylist(Long id){
        Playlist playlist = playlistRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
        playlistRepository.delete(playlist);
    }

    public Iterable<Playlist> playlists() {
        return playlistRepository.findByUser(sessionService.findUser());
    }
}
