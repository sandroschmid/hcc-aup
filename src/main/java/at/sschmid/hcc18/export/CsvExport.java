package at.sschmid.hcc18.export;

import at.sschmid.hcc18.model.Cluster;

final class CsvExport extends Export {

  CsvExport() {
    super("csv");
  }

  @Override
  String getContents(final Cluster cluster) {
    return cluster.asCsv();
  }

}