package tech.makers.aceplay.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.EmptyFieldException;
import tech.makers.aceplay.session.SessionService;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TrackService {
    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private SessionService sessionService;

    public Track createTrack(TrackRequestModel trackRequestModel){
        if (trackRequestModel.getArtist() == null || trackRequestModel.getArtist().isEmpty() || trackRequestModel.getArtist().trim().isEmpty()) {
            throw new EmptyFieldException("Empty Artist");
        }else{
            if (trackRequestModel.getTitle() == null || trackRequestModel.getTitle().isEmpty() || trackRequestModel.getTitle().trim().isEmpty()) {
                throw new EmptyFieldException("Empty Title");
            }else {
                Track track = new Track(trackRequestModel.getTitle(), trackRequestModel.getArtist(), trackRequestModel.getPublicUrl());
                track.setUser(sessionService.findUser());
                return trackRepository.save(track);
            }
        }
    }

    public void deleteTrack(Long id){
        Track track = trackRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + id));
        trackRepository.delete(track);
    }
}
