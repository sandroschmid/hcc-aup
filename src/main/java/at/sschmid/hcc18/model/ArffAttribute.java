package at.sschmid.hcc18.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ArffAttribute {
  
  private static final String TYPE_NUM = "numeric";
  
  final String key;
  
  private final boolean isNumeric;
  private final Set<String> allowedValues;
  
  ArffAttribute(final String key, final String data) {
    this.key = key.toUpperCase();
    if (data.startsWith("{") && data.endsWith("}")) {
      this.allowedValues = Arrays
          .stream(data.substring(1, data.length() - 1).split(","))
          .collect(Collectors.toSet());
      this.isNumeric = false;
    } else {
      this.allowedValues = null;
      this.isNumeric = TYPE_NUM.equals(data);
    }
  }
  
  public boolean hasAllowedValues() {
    return allowedValues != null && !allowedValues.isEmpty();
  }
  
  public boolean isValueAllowed(final String key) {
    return !hasAllowedValues() || allowedValues.contains(key);
  }
  
}
