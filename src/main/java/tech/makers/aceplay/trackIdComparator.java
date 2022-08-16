package tech.makers.aceplay;

import tech.makers.aceplay.track.Track;

import java.util.Comparator;

public class trackIdComparator implements Comparator<Track> {
    public int compare(Track t1, Track t2){
      return (int) (t1.getId() - t2.getId());
    }
  }
