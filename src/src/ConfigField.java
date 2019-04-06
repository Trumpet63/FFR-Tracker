package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ConfigField {
    API_KEY("api key"),
    USERNAME("username"),
    MAX_SCORES_TO_LOAD("max scores to load"),
    DATE_FORMAT("date format"),
    BEST_AAA_EQUIVALENT("best aaa equivalent");
 
    private final String fieldName;
    private static final Map<String, ConfigField> lookup = new HashMap<>();
    public static final List<String> ALL_FIELD_NAMES = new ArrayList<>();
 
    ConfigField(String fieldName) {
        this.fieldName = fieldName;
    }
 
    public String getFieldName() {
        return fieldName;
    }
  
    
    static {
        for(ConfigField field : ConfigField.values()) {
            lookup.put(field.getFieldName(), field);
            ALL_FIELD_NAMES.add(field.getFieldName());
        }
    }
  
    public static ConfigField get(String fieldName) {
        return lookup.get(fieldName);
    }
}
