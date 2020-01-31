package at.sschmid.hcc18;

import lombok.extern.slf4j.Slf4j;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

import java.io.File;

@Slf4j
public class WekaApplication {
  
  private static final String DIR = "files/data";
  private static final String FILE_AGGR = "pslc_aggregated.arff";
  private static final String FILE_EXTENDED = "pslc_extended.arff";
  private static final int K = 4;
  private static final int MAX_ITERATIONS = 500;
  
  public static void main(final String[] args) {
    final File file = new File(DIR, FILE_AGGR);
    try {
      final Loader loader = new ArffLoader();
      loader.setSource(file);
      final Instances data = loader.getDataSet();
      
      final SimpleKMeans kMeans = new SimpleKMeans();
      kMeans.setNumClusters(K);
      kMeans.setSeed(10);
      kMeans.setMaxIterations(MAX_ITERATIONS);
      kMeans.setPreserveInstancesOrder(true);
      kMeans.buildClusterer(data);
      log.info(kMeans.toString());
//      final int[] clusterSizes = kMeans.getClusterSizes();
//      final Instances centroids = kMeans.getClusterCentroids();
//      for (int i = 0; i < K; i++) {
//        log.info(String.format("Cluster #%d: %d instance(s)", i, clusterSizes[i]));
//        log.info(String.format("  Centroid: %s", centroids.instance(i)));
//      }
      
    } catch (final Exception e) {
      log.error("An error occurred", e);
    }
  }
  
}
