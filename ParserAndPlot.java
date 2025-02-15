import core.data.*;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ParserAndPlot extends ApplicationFrame {
  DataSource ds;
  List<Quake> latest;
  double[] magnitudeList;
  public ParserAndPlot (final String title) {
    super(title);
    
    this.ds = DataSource.connect("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.geojson"); // or use ...all_day, etc.
    ds.setCacheTimeout(300);    
    
    ds.load();
    
    this.latest = ds.fetchList("Quake",
            "features/properties/title",
            "features/properties/time",
            "features/properties/mag",
            "features/properties/url");
    this.magnitudeList = new double[latest.size()];

    IntervalXYDataset dataset = createDataset();
    JFreeChart chart = ChartFactory.createXYBarChart(
      "Recent Earthquake Magnitudes (1 Month)",
      "Total Number of Quakes: " + magnitudeList.length, 
      false,
      "Frequency", 
      dataset,
      PlotOrientation.VERTICAL,
      true,
      true,
      false
    );
    final ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(775, 270));
    setContentPane(chartPanel);
  }

  
  
  private IntervalXYDataset createDataset() {
    double largest = latest.get(0).magnitude;
    double smallest = latest.get(0).magnitude;
    for (int i = 0; i < latest.size(); i++) {
      double mag = latest.get(i).magnitude;
      magnitudeList[i] = mag;
      if (mag > largest) {
        largest = mag;
      }
      if (mag < smallest) {
        smallest = mag;
      }
    }

    HistogramDataset dataset = new HistogramDataset();
    dataset.setType(HistogramType.RELATIVE_FREQUENCY);
    int numBins = (int) Math.ceil((largest - smallest) / 0.5);
    dataset.addSeries("Magnitudes", magnitudeList, numBins, Math.floor(smallest * 2) / 2, Math.ceil(largest * 2) / 2); 
    System.out.println(magnitudeList.length);
    return dataset;     
  }

  public static void main(String[] args) {
    final ParserAndPlot plt = new ParserAndPlot("Earthquake Intensity");
    plt.pack();
    RefineryUtilities.centerFrameOnScreen(plt);
    plt.setVisible(true);

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