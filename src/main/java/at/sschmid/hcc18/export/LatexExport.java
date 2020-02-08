package at.sschmid.hcc18.export;

import at.sschmid.hcc18.Cluster;

class LatexExport extends Export {

  LatexExport() {
    super("tex");
  }

  @Override
  String getContents(final Cluster cluster) {
    return cluster.asLatexTable();
  }

}
