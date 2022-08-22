package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;


import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PlaylistService {
    public static final String NOPLAYLIST = "No playlist exists with id ";

    @Autowired PlaylistRepository playlistRepository;

    @Autowired TrackRepository trackRepository;

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

    public Playlist findPlaylist(Long id){
        return playlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
    }

    public void deletePlaylist(Long id){
        Playlist playlist = playlistRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
        playlistRepository.delete(playlist);
    }

    public void deleteTracks(Long playlistId, Long trackId){
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + playlistId));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + trackId));
        playlist.getTracks().remove(track);
        playlistRepository.save(playlist);
    }

    public Track addTrack(Long id, TrackIdentifierDto trackIdentifierDto){
        Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOPLAYLIST + id));
    Track track = trackRepository.findById(trackIdentifierDto.getId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + trackIdentifierDto.getId()));
    playlist.getTracks().add(track);
    playlistRepository.save(playlist);
    return track;
    }

    public Iterable<Playlist> playlists() {
        return playlistRepository.findByUser(sessionService.findUser());
    }
}
