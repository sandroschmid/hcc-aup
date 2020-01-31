package at.sschmid.hcc18;

import at.sschmid.hcc18.model.ArffAttribute;
import at.sschmid.hcc18.model.ArffFile;
import lombok.extern.slf4j.Slf4j;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class Application {
  
  private static final String DIR = "files/data";
  private static final String FILE_AGGR = "pslc_aggregated.arff";
  private static final String FILE_EXTENDED = "pslc_extended.arff";
  private static final int K = 4;
  private static final int MAX_ITERATIONS = 500;
  
  public static void main(final String[] args) {
    final ArffFile arffFile = new ArffFile(DIR, FILE_AGGR);
    try {
      arffFile.read();
    } catch (final Exception e) {
      log.error("Could not read Arff-File", e);
      return;
    }
    
    final List<ArffAttribute> attributes = arffFile.getAttributes();
    final List<List<Object>> data = arffFile.getRows();
    
    log.info(String.format("'%s' has %d attributes and %d data rows",
        arffFile.getFileName(),
        attributes.size(),
        data.size()));
    
    final Dataset dataset = arffFile.getDataset();
    final DistanceMeasure distance = new EuclideanDistance();
//    final DistanceMeasures distance = new NormalizedEuclideanDistance(dataset);
    final KMeans kMeans = new KMeans(K, MAX_ITERATIONS, distance);
    final Dataset[] clusters = kMeans.cluster(dataset);
    log.info(String.format("%d clusters found, distance=%s",
        clusters.length,
        distance.getClass().getSimpleName()));
    
    for (int i = 0; i < clusters.length; i++) {
      final Dataset cluster = clusters[i];
      log.info(String.format("Custer #%d: %d items", i, cluster.size()));
    }
    
    try {
      final Field centroidsField = kMeans.getClass().getDeclaredField("centroids");
      centroidsField.setAccessible(true);
      final Instance[] centroids = (Instance[]) centroidsField.get(kMeans);
      for (int i = 0; i < centroids.length; i++) {
        final Instance centroid = centroids[i];
        log.info(String.format("Centroid #%d: %s", i, centroid.getID()));
      }
    } catch (final IllegalAccessException | NoSuchFieldException e) {
      log.error("Could not get centroids", e);
    }
    
  }
  
}
