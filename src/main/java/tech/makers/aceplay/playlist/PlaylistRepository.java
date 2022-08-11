package tech.makers.aceplay.playlist;

import org.springframework.data.repository.CrudRepository;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=343s
public interface PlaylistRepository extends CrudRepository<Playlist, Long> {
  Playlist findFirstByOrderByIdAsc();

}
