package tech.makers.aceplay.playlist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=343s

public interface PlaylistRepository extends CrudRepository<Playlist, Long> {
  Playlist findFirstByOrderByIdAsc();

  @Query(value = "SELECT * FROM playlist WHERE cool = false",nativeQuery = true)
  List<Playlist> findAllUncool();

  @Query(value = "SELECT * FROM playlist WHERE cool = true", nativeQuery = true)
  List<Playlist> findAllCool();
}


