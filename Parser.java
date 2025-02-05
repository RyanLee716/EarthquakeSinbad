import core.data.*;
import java.util.*;

public class Parser {
  public static void main(String[] args) {
    int DELAY = 300;   // 5 minute cache delay
    
    DataSource ds = DataSource.connect("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson"); // or use ...all_day, etc.
    ds.setCacheTimeout(DELAY);    
    
    ds.load();
    
    List<Quake> latest = ds.fetchList("Quake",
            "features/properties/title",
            "features/properties/time",
            "features/properties/mag",
            "features/properties/url");
    for (Quake q : latest) {
        System.out.println(q.description + " (" + q.date() + ") info at: " + q.url);
        System.out.println(q.magnitude);
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
}