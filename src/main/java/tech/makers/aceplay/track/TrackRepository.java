package tech.makers.aceplay.track;

import org.springframework.data.repository.CrudRepository;
import tech.makers.aceplay.playlist.Playlist;
import tech.makers.aceplay.user.User;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2856s
public interface TrackRepository extends CrudRepository<Track, Long> {
  Track findFirstByOrderByIdAsc();

  Iterable<Track> findByUser(User user);

}
