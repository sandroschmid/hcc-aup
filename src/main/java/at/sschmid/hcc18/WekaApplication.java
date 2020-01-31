package at.sschmid.hcc18;

import lombok.extern.slf4j.Slf4j;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class WekaApplication {
  
  private static final String DATA_DIR = "files/data";
  private static final String OUT_DIR = "files/out";
  private static final String FILE_AGGR = "pslc_aggregated.arff";
  private static final String FILE_EXTENDED = "pslc_extended.arff";
  private static final int MIN_K = 2;
  private static final int MAX_K = 8;
  private static final int MAX_ITERATIONS = 500;
  
  public static void main(final String[] args) throws Exception {
    final File fileAggr = new File(DATA_DIR, FILE_AGGR);
    final File fileExtended = new File(DATA_DIR, FILE_EXTENDED);
    
    final Instances dataAggr = getData(fileAggr);
    final Instances dataExtended = getData(fileExtended);
    
    process(dataAggr, String.format("%s/aggr/full", OUT_DIR));
    process(dataExtended, String.format("%s/extended/full", OUT_DIR));
    
    final Instances dataAggrNoStats = removeStatistics(dataAggr);
    final Instances dataExtendedNoStats = removeStatistics(dataExtended);
    
    process(dataAggrNoStats, String.format("%s/aggr/no-stat", OUT_DIR));
    process(dataExtendedNoStats, String.format("%s/extended/no-stat", OUT_DIR));
    
    final Set<String> aggrBlacklist = new HashSet<>();
//    aggrBlacklist.add("TRANS_PROB_0_0"); // richtig - richtig
//    aggrBlacklist.add("TRANS_PROB_0_1"); // richtig - falsch
    aggrBlacklist.add("TRANS_PROB_0_2"); // richtig - hilfe
//    aggrBlacklist.add("TRANS_PROB_0_3"); // richtig - fertig

//    aggrBlacklist.add("TRANS_PROB_1_0"); // falsch - richtig
//    aggrBlacklist.add("TRANS_PROB_1_1"); // falsch - falsch
//    aggrBlacklist.add("TRANS_PROB_1_2"); // falsch - hilfe
    aggrBlacklist.add("TRANS_PROB_1_3"); // falsch - fertig

//    aggrBlacklist.add("TRANS_PROB_2_0"); // hilfe - richtig
//    aggrBlacklist.add("TRANS_PROB_2_1"); // hilfe - falsch
//    aggrBlacklist.add("TRANS_PROB_2_2"); // hilfe - hilfe
    aggrBlacklist.add("TRANS_PROB_2_3"); // hilfe - fertig
    
    aggrBlacklist.add("TRANS_PROB_3_0"); // fertig - richtig
    aggrBlacklist.add("TRANS_PROB_3_1"); // fertig - falsch
    aggrBlacklist.add("TRANS_PROB_3_2"); // fertig - hilfe
    aggrBlacklist.add("TRANS_PROB_3_3"); // fertig - fertig
    
    final Set<String> extendedBlacklist = new HashSet<>();
//    extendedBlacklist.add("TRANS_PROB_0_0"); // richtig - richtig
//    extendedBlacklist.add("TRANS_PROB_0_1"); // richtig - falsch
    extendedBlacklist.add("TRANS_PROB_0_2"); // richtig - hilfe 1
    extendedBlacklist.add("TRANS_PROB_0_3"); // richtig - hilfe 2
    extendedBlacklist.add("TRANS_PROB_0_4"); // richtig - hilfe 3
    extendedBlacklist.add("TRANS_PROB_0_5"); // richtig - hilfe 4
//    extendedBlacklist.add("TRANS_PROB_0_6"); // richtig - fertig

//    extendedBlacklist.add("TRANS_PROB_1_0"); // falsch - richtig
//    extendedBlacklist.add("TRANS_PROB_1_1"); // falsch - falsch
//    extendedBlacklist.add("TRANS_PROB_1_2"); // falsch - hilfe 1
//    extendedBlacklist.add("TRANS_PROB_1_3"); // falsch - hilfe 2
//    extendedBlacklist.add("TRANS_PROB_1_4"); // falsch - hilfe 3
//    extendedBlacklist.add("TRANS_PROB_1_5"); // falsch - hilfe 4
    extendedBlacklist.add("TRANS_PROB_1_6"); // falsch - fertig

//    extendedBlacklist.add("TRANS_PROB_2_0"); // hilfe 1 - richtig
//    extendedBlacklist.add("TRANS_PROB_2_1"); // hilfe 1 - falsch
    extendedBlacklist.add("TRANS_PROB_2_2"); // hilfe 1 - hilfe 1
//    extendedBlacklist.add("TRANS_PROB_2_3"); // hilfe 1 - hilfe 2
//    extendedBlacklist.add("TRANS_PROB_2_4"); // hilfe 1 - hilfe 3
//    extendedBlacklist.add("TRANS_PROB_2_5"); // hilfe 1 - hilfe 4
    extendedBlacklist.add("TRANS_PROB_2_6"); // hilfe 1 - fertig

//    extendedBlacklist.add("TRANS_PROB_3_0"); // hilfe 2 - richtig
//    extendedBlacklist.add("TRANS_PROB_3_1"); // hilfe 2 - falsch
//    extendedBlacklist.add("TRANS_PROB_3_2"); // hilfe 2 - hilfe 1
    extendedBlacklist.add("TRANS_PROB_3_3"); // hilfe 2 - hilfe 2
//    extendedBlacklist.add("TRANS_PROB_3_4"); // hilfe 2 - hilfe 3
//    extendedBlacklist.add("TRANS_PROB_3_5"); // hilfe 2 - hilfe 4
    extendedBlacklist.add("TRANS_PROB_3_6"); // hilfe 2 - fertig

//    extendedBlacklist.add("TRANS_PROB_4_0"); // hilfe 3 - richtig
//    extendedBlacklist.add("TRANS_PROB_4_1"); // hilfe 3 - falsch
//    extendedBlacklist.add("TRANS_PROB_4_2"); // hilfe 3 - hilfe 1
//    extendedBlacklist.add("TRANS_PROB_4_3"); // hilfe 3 - hilfe 2
    extendedBlacklist.add("TRANS_PROB_4_4"); // hilfe 3 - hilfe 3
//    extendedBlacklist.add("TRANS_PROB_4_5"); // hilfe 3 - hilfe 4
    extendedBlacklist.add("TRANS_PROB_4_6"); // hilfe 3 - fertig

//    extendedBlacklist.add("TRANS_PROB_5_0"); // hilfe 4 - richtig
//    extendedBlacklist.add("TRANS_PROB_5_1"); // hilfe 4 - falsch
//    extendedBlacklist.add("TRANS_PROB_5_2"); // hilfe 4 - hilfe 1
//    extendedBlacklist.add("TRANS_PROB_5_3"); // hilfe 4 - hilfe 2
//    extendedBlacklist.add("TRANS_PROB_5_4"); // hilfe 4 - hilfe 3
    extendedBlacklist.add("TRANS_PROB_5_5"); // hilfe 4 - hilfe 4
    extendedBlacklist.add("TRANS_PROB_5_6"); // hilfe 4 - fertig
    
    extendedBlacklist.add("TRANS_PROB_6_0"); // fertig - richtig
    extendedBlacklist.add("TRANS_PROB_6_1"); // fertig - falsch
    extendedBlacklist.add("TRANS_PROB_6_2"); // fertig - hilfe 1
    extendedBlacklist.add("TRANS_PROB_6_3"); // fertig - hilfe 2
    extendedBlacklist.add("TRANS_PROB_6_4"); // fertig - hilfe 3
    extendedBlacklist.add("TRANS_PROB_6_5"); // fertig - hilfe 4
    extendedBlacklist.add("TRANS_PROB_6_6"); // fertig - fertig
    
    final Instances dataAggrFinal = remove(dataAggr, aggrBlacklist);
    final Instances dataExtendedFinal = remove(dataExtended, extendedBlacklist);
    
    process(dataAggrFinal, String.format("%s/aggr/final", OUT_DIR));
    process(dataExtendedFinal, String.format("%s/extended/final", OUT_DIR));
  }
  
  private static Instances getData(final File file) throws Exception {
    final Loader loader = new ArffLoader();
    loader.setSource(file);
    return loader.getDataSet();
  }
  
  private static Instances removeStatistics(final Instances data) {
    final Enumeration<Attribute> attributesIt = data.enumerateAttributes();
    int deletedAttributes = 0;
    while (attributesIt.hasMoreElements()) {
      final Attribute attribute = attributesIt.nextElement();
      final String attrName = attribute.name().toLowerCase();
      if (attrName.contains("mean") || attrName.contains("median") || attrName.contains("stdev")) {
        data.deleteAttributeAt(attribute.index() - deletedAttributes++);
      }
    }
    
    return data;
  }
  
  private static Instances remove(final Instances data, final Set<String> blacklist) {
    final Enumeration<Attribute> attributesIt = data.enumerateAttributes();
    int deletedAttributes = 0;
    while (attributesIt.hasMoreElements()) {
      final Attribute attribute = attributesIt.nextElement();
      if (blacklist.contains(attribute.name())) {
        data.deleteAttributeAt(attribute.index() - deletedAttributes++);
      }
    }
    
    return data;
  }
  
  private static void process(final Instances data, final String outDir) throws Exception {
    for (int k = MIN_K; k <= MAX_K; k++) {
      final Cluster cluster = new Cluster(k, MAX_ITERATIONS);
      cluster.cluster(data);
      Export.Csv.export(outDir, cluster);
      Export.Latex.export(outDir, cluster);
    }
  }
  
}
