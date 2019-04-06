package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;

public class SongList {
    private static Map<Integer, Song> songList;
    public static boolean initialized = false;
    
    public static void init() {
        try {
            LogController.LOGGER.info("Getting song list...");
            URL songList = getSongListURL();
            JSONArray json = getSongListAsJSON(songList);
            LogController.LOGGER.info("Storing song list...");
            storeSongList(json);
            initialized = true;
            LogController.LOGGER.info("Song list stored successfully!");
        }
        catch(Exception ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private static URL getSongListURL() {
        try {
            return new URL("http://www.flashflashrevolution.com/api/api.php?key=" +
                    Config.API_KEY +
                    "&action=songlist"
            );
        } 
        catch (MalformedURLException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static JSONArray getSongListAsJSON(URL songList) {
        LogController.LOGGER.info("Accessing FFR API...");
        try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(songList.openStream()))) {
            StringBuilder wholeString = new StringBuilder();
            String inputLine;
            LogController.LOGGER.info("Reading song list...");
            while ((inputLine = in.readLine()) != null)
                wholeString.append(inputLine);
            return new JSONArray(wholeString.toString());
        } catch (IOException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static void storeSongList(JSONArray json) {
        songList = new HashMap<>();
        for(int i = 0; i < json.length(); i++) {
            JSONObject song = json.getJSONObject(i);
            songList.put(song.getInt(SongProperty.ID.getPropertyName()), new Song(song));
        }
    }
    
    public static Song getSong(int id) {
        return songList.get(id);
    }
}
