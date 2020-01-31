package at.sschmid.hcc18.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ArffFile {
  
  private static final String ANNOTATION_ATTR = "@attribute";
  private static final String ANNOTATION_DATA = "@data";
  
  @Getter
  private final String path;
  
  @Getter
  private final String fileName;
  
  private final List<ArffAttribute> attributes = new ArrayList<>();
  private final List<List<Object>> rows = new LinkedList<>();
  
  public ArffFile(final String path, final String fileName) {
    this.path = path;
    this.fileName = fileName;
  }
  
  public List<ArffAttribute> getAttributes() {
    return new ArrayList<>(attributes);
  }
  
  public List<List<Object>> getRows() {
    return new ArrayList<>(rows);
  }
  
  public Dataset getDataset() {
    final Dataset dataset = new DefaultDataset();
    final int cntNumAttrs = (int) attributes.stream().filter(ArffAttribute::isNumeric).count();
    for (final List<Object> row : rows) {
      final double[] values = new double[cntNumAttrs];
      int i = 0;
      for (final Object value : row) {
        if (value instanceof Double) {
          values[i++] = (double) value;
        }
      }
      
      dataset.add(new DenseInstance(values));
    }
    
    return dataset;
  }
  
  public void read() throws FileNotFoundException {
    final File file = new File(path, fileName);
    if (!file.exists()) {
      throw new FileNotFoundException(String.format("%s not found", file.getPath()));
    }
    
    if (!file.isFile()) {
      throw new IllegalArgumentException(String.format("%s is not a file", file.getPath()));
    }
    
    try (final Scanner scanner = new Scanner(file)) {
      boolean hasDataStarted = false;
      while (scanner.hasNextLine()) {
        final String line = scanner.nextLine();
        if (line != null && !line.isEmpty()) {
          hasDataStarted = processLine(line, hasDataStarted);
        }
      }
    }
  }
  
  private boolean processLine(final String line, final boolean hasDataStarted) {
    if (line.startsWith(ANNOTATION_ATTR)) {
      processAttribute(line);
    } else if (line.equals(ANNOTATION_DATA)) {
      return true;
    } else if (hasDataStarted) {
      processData(line);
    }
    
    return hasDataStarted;
  }
  
  private void processAttribute(final String line) {
    final String[] lineData = line.split(" ");
    if (lineData.length != 3) {
      throw new RuntimeException(
          String.format("Line '%s' was expected to have 3 parts but had %d", line, lineData.length));
    }
    
    attributes.add(new ArffAttribute(lineData[1], lineData[2]));
  }
  
  private void processData(final String line) {
    final String[] values = line.split(",");
    final List<Object> row = new ArrayList<>(values.length);
    for (int i = 0; i < values.length; i++) {
      final String value = values[i];
      final ArffAttribute attribute = attributes.get(i);
      if (attribute.isNumeric()) {
        try {
          final double numValue = Double.parseDouble(value);
          row.add(numValue);
        } catch (final NumberFormatException e) {
          row.add(0d);
        }
      } else if (attribute.isValueAllowed(value)) {
        row.add(value);
      } else {
        throw new RuntimeException(String.format("No attribute for line[%d]='%s'", i, value));
      }
    }
    
    rows.add(row);
  }
  
}
