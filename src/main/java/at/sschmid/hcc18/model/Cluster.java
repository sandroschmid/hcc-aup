package at.sschmid.hcc18.model;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Locale;

public class Cluster {

  private static final NumberFormat DECIMAL_FORMAT;
  private static final int MAX_ATTR_PER_PAGE = 22;

  static {
    DECIMAL_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    DECIMAL_FORMAT.setMaximumFractionDigits(6);
  }

  private final int k;
  private final SimpleKMeans kMeans;

  private Instances data;

  public Cluster(final int k, final int maxIterations) throws Exception {
    this.k = k;
    this.kMeans = createKMeans(k, maxIterations);
  }

  private SimpleKMeans createKMeans(final int k, final int maxIterations) throws Exception {
    final SimpleKMeans kMeans = new SimpleKMeans();
    kMeans.setNumClusters(k);
    kMeans.setSeed(10);
    kMeans.setMaxIterations(maxIterations);
    kMeans.setPreserveInstancesOrder(true);
    return kMeans;
  }

  public int k() {
    return k;
  }

  public void cluster(final Instances data) throws Exception {
    this.data = data;

    kMeans.setDistanceFunction(new EuclideanDistance(data));
    kMeans.buildClusterer(data);
  }

  @Override
  public String toString() {
    return kMeans.toString();
  }

  public String asLatexTable() {
    final StringBuilder tableStart = new StringBuilder("\\begin{sidewaystable}")
        .append("\n  \\centering")
        .append("\n  \\begin{tabularx}{\\textwidth}{|*{")
        .append(k + 1)
        .append("}{>{\\RaggedRight\\arraybackslash}X|}}")
        .append("\n    \\hline")
        .append("\n    Attribute");
    for (int i = 1; i <= k; i++) {
      tableStart.append(" & Cluster ").append(i);
    }

    tableStart.append(" \\\\\\hline");

    final StringBuilder pages = new StringBuilder();
    final int numAttributes = data.numAttributes();
    final int cntPages = (int) Math.ceil(numAttributes / (double) MAX_ATTR_PER_PAGE);
    for (int pageIdx = 0; pageIdx < cntPages; pageIdx++) {
      if (pageIdx > 0) {
        pages.append("\n");
      }

      pages.append(tableStart)
          .append(latexTablePage(pageIdx * (MAX_ATTR_PER_PAGE + 1)))
          .append(latexTableEnd(pageIdx + 1));
    }

    return pages.toString();
  }

  private String latexTablePage(final int attrStart) {
    final StringBuilder page = new StringBuilder();
    final Instances centroids = kMeans.getClusterCentroids();
    final int numAttributes = data.numAttributes();
    final Enumeration<Attribute> attributeIt = data.enumerateAttributes();
    final int lastAttrIdx = Math.min(numAttributes - 1, attrStart + MAX_ATTR_PER_PAGE);
    int attrIdx = 0;
    while (attrIdx < attrStart && attributeIt.hasMoreElements()) {
      attributeIt.nextElement();
      attrIdx++;
    }

    while (attributeIt.hasMoreElements()) {
      final Attribute attribute = attributeIt.nextElement();
      final String attrName = attribute.name().replace("_", "\\_");
      page.append("\n    ").append(attrName).append(" & ");

      for (int cIdx = 0; cIdx < k; cIdx++) {
        final Instance centroid = centroids.instance(cIdx);
        page.append(instanceToString(centroid, attribute)).append(" & ");
      }

      page.replace(page.length() - " & ".length(), page.length(), " \\\\\\hline");

      if (attrIdx == lastAttrIdx) {
        break;
      } else {
        attrIdx++;
      }
    }

    return page.toString();
  }

  private String latexTableEnd(final int page) {
    return new StringBuilder()
        .append("\n  \\end{tabularx}")
        .append("\n  \\caption{KMeans $k=").append(k).append("$ (S").append(page).append(")}")
        .append("\n  \\label{tab:kmeans-k").append(k).append("-p").append(page).append("}")
        .append("\n\\end{sidewaystable}\n")
        .toString();
  }

  public String asCsv() {
    final StringBuilder csv = new StringBuilder("Attribute");
    for (int i = 1; i <= k; i++) {
      csv.append(",Cluster").append(i);
    }

    final Instances centroids = kMeans.getClusterCentroids();
    final Enumeration<Attribute> attributeIt = data.enumerateAttributes();
    while (attributeIt.hasMoreElements()) {
      final Attribute attribute = attributeIt.nextElement();
      final String attrName = attribute.name();
      csv.append("\n").append(attrName).append(",");

      for (int cIdx = 0; cIdx < k; cIdx++) {
        final Instance centroid = centroids.instance(cIdx);
        csv.append(instanceToString(centroid, attribute)).append(",");
      }

      csv.deleteCharAt(csv.length() - 1);
    }

    return csv.toString();
  }

  private String instanceToString(final Instance instance, final Attribute attribute) {
    if (instance.isMissing(attribute)) {
      return attribute.isNumeric() ? "NaN" : "-";
    } else if (attribute.isNumeric()) {
      final double value = instance.value(attribute);
      return DECIMAL_FORMAT.format(value);
    } else {
      return instance.stringValue(attribute);
    }
  }

}
