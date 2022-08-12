package tech.makers.aceplay.track;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2856s
public interface TrackRepository extends CrudRepository<Track, Long> {
  Track findFirstByOrderByIdAsc();

//  Track findByIdOrderByIdDesc();

//  List<Track> findByIdOrderByIdAsc(Long id);

//  Track findAllByOrderByDesc();

//  List<Track> findAllByOrderByIdAsc();

//  @Query(value = "SELECT * FROM playlist WHERE cool = false",nativeQuery = true)
//  List<Track> findAllByDesc();
}
