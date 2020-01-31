package at.sschmid.hcc18;

import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

import java.io.File;

@Slf4j
public class WekaApplication {
  
  private static final String DATA_DIR = "files/data";
  private static final String OUT_DIR = "files/out";
  private static final String FILE_AGGR = "pslc_aggregated.arff";
  private static final String FILE_EXTENDED = "pslc_extended.arff";
  private static final int MIN_K = 4;
  private static final int MAX_K = 4;
  private static final int MAX_ITERATIONS = 500;
  
  public static void main(final String[] args) throws Exception {
    final File fileAggr = new File(DATA_DIR, FILE_AGGR);
    final File fileExtended = new File(DATA_DIR, FILE_EXTENDED);
    
    process(fileAggr, String.format("%s/aggr", OUT_DIR));
    process(fileExtended, String.format("%s/extended", OUT_DIR));
  }
  
  private static void process(final File file, final String outDir) throws Exception {
    final Loader loader = new ArffLoader();
    loader.setSource(file);
    final Instances data = loader.getDataSet();
    // TODO: delete attributes
  
    for (int k = MIN_K; k <= MAX_K; k++) {
      final Cluster cluster = new Cluster(k, MAX_ITERATIONS);
      cluster.cluster(data);
      Export.Csv.export(outDir, cluster);
      Export.Latex.export(outDir, cluster);
    }
  }
  
}
