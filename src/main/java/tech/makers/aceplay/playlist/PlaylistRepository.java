package tech.makers.aceplay.playlist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.makers.aceplay.user.User;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=343s

public interface PlaylistRepository extends CrudRepository<Playlist, Long> {
  Playlist findFirstByOrderByIdAsc();

  Iterable<Playlist> findByUser(User user);


}
