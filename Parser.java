import core.data.*;
import java.util.*;

public class Parser {
  public static void main(String[] args) {
    int DELAY = 300;   // 5 minute cache delay
    
    DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"); // or use ...all_day, etc.
    ds.setCacheTimeout(DELAY);    
    
    ds.load();
    
    List<Quake> latest = ds.fetchList("Quake",
            "features/properties/title",
            "features/properties/time",
            "features/properties/mag",
            "features/properties/url");
    for (Quake q : latest) {
        System.out.println(q.description + " (" + q.date() + ") info at: " + q.url);
    }
    
    ds.printUsageString();

  }
}


class Quake {
  String description;
  long timestamp;
  float magnitude;
  String url;
  
  public Quake(String description, long timestamp, float magnitude, String url) {
    this.description = description;
    this.timestamp = timestamp;
    this.magnitude = magnitude;
    this.url = url;
  }
  
  public Date date() {
    return new Date(timestamp);
  }
  
  public boolean equals(Object o) {
    if (o.getClass() != this.getClass()) 
      return false;
    Quake that = (Quake) o;
    return that.description.equals(this.description)
      && that.timestamp == this.timestamp
      && that.magnitude == this.magnitude;
  }
  
  public int hashCode() {                
    return (int) (31 * (31 * this.description.hashCode()
                          + this.timestamp) + this.magnitude);
  }
}