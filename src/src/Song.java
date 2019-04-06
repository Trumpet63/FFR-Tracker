package src;

import org.json.JSONObject;

public class Song {
    int id;
    String name;
    int genre;
    int difficulty;
    String length;
    int noteCount;
    long timestamp;
    
    public Song(JSONObject songJSON) {
        this.id = songJSON.getInt(SongProperty.ID.getPropertyName());
        this.name = songJSON.getString(SongProperty.NAME.getPropertyName());
        this.genre = songJSON.getInt(SongProperty.GENRE.getPropertyName());
        this.difficulty = songJSON.getInt(SongProperty.DIFFICULTY.getPropertyName());
        this.length = songJSON.getString(SongProperty.LENGTH.getPropertyName());
        this.noteCount = songJSON.getInt(SongProperty.NOTE_COUNT.getPropertyName());
        this.timestamp = songJSON.getLong(SongProperty.TIMESTAMP.getPropertyName());
    }
}
