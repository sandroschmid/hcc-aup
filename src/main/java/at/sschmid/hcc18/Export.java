package at.sschmid.hcc18;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public enum Export {
  Latex("tex"),
  Csv("csv");
  
  private final String fileExtension;
  
  Export(final String fileExtension) {
    this.fileExtension = fileExtension;
  }
  
  public String getFileExtension() {
    return fileExtension;
  }
  
  public void export(final String outDirPath, final String fileNamePrefix, final Cluster cluster) throws IOException {
    final String content;
    if (Latex.equals(this)) {
      content = cluster.asLatexTable();
    } else {
      content = cluster.asCsv();
    }
  
    final File outDir = new File(outDirPath, fileExtension);
    if (!outDir.exists()) {
      outDir.mkdirs();
    }
  
    final String fileName = String.format("%s_cluster-k%d.%s", fileNamePrefix, cluster.k(), fileExtension);
    final File outFile = new File(outDir, fileName);
    try (final Writer writer = new FileWriter(outFile, false)) {
      writer.write(content);
    }
  }
}
