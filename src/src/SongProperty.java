package src;

import java.util.HashMap;
import java.util.Map;

public enum SongProperty {
    ID("id"),
    NAME("name"),
    GENRE("genre"),
    DIFFICULTY("difficulty"),
    LENGTH("length"),
    NOTE_COUNT("note_count"),
    TIMESTAMP("timestamp");
    
    private final String propertyName;
    private static final Map<String, SongProperty> lookup = new HashMap<>();
    
    SongProperty(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    static {
        for(SongProperty property : SongProperty.values()) {
            lookup.put(property.getPropertyName(), property);
        }
    }
    
    public static SongProperty get(String propertyName) {
        return lookup.get(propertyName);
    }
}
