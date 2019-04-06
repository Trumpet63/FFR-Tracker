package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Crawler {
    
    public static JSONArray getRecentGamesAndExtraInfo() {
        LogController.LOGGER.info("Getting recent games...");
        URL recentGamesURL = getRecentGamesURL();
        LogController.LOGGER.info("Accesssing FFR API...");
        JSONArray json = getRecentGamesAsJSON(recentGamesURL);
        LogController.LOGGER.info("Getting additional song information...");
        fetchAdditionalSongInformation(json);
        LogController.LOGGER.info("Calculating AAA Equivalents...");
        getAAAEquivalents(json);
        LogController.LOGGER.info("Got recent games!");
        return json;
    }
    
    private static URL getRecentGamesURL() {
        try {
            return new URL("http://www.flashflashrevolution.com/api/api.php?key=" +
                    Config.API_KEY +
                    "&action=recent_games&username=" +
                    Config.USERNAME);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static JSONArray getRecentGamesAsJSON(URL recentGames) {
        try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(recentGames.openStream()))) {
            StringBuilder wholeString = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                wholeString.append(inputLine);
            return new JSONObject(wholeString.toString()).getJSONArray("recent");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static void fetchAdditionalSongInformation(JSONArray games) {
        for(int i = 0; i < games.length(); i++) {
            JSONObject game = games.getJSONObject(i);
            int songID = game.getInt("level");
            Song song = SongList.getSong(songID);
            game.put("note_count", song.noteCount);
            game.put("difficulty", song.difficulty);
        }
    }
    
    private static void getAAAEquivalents(JSONArray games) {
        for(int i = 0; i < games.length(); i++) {
            JSONObject game = games.getJSONObject(i);
            getAAAEquivalentForGame(game);
        }
    }
    
    private static void getAAAEquivalentForGame(JSONObject game) {
        int numPerfect = game.getInt("perfect");
        int numGood = game.getInt("good");
        int numAverage = game.getInt("average");
        int numMiss = game.getInt("miss");
        int numBoo = game.getInt("boo");
        int noteCount = game.getInt("note_count");
        int difficulty = game.getInt("difficulty");
        
        boolean completed = gameCompleted(numPerfect, numGood, numAverage, numMiss, noteCount);
        game.put("completed", completed);
        double AAAEquivalent = -1.0;
        if(completed) {
            double newGoodCount = getNewGoodCount(numGood, numAverage, numMiss, numBoo);
            AAAEquivalent = getAAAEquivalent(newGoodCount, (double)difficulty);
        }
        game.put("aaa_equivalent", AAAEquivalent);
    }
    
    private static boolean gameCompleted(int numPerfect, int numGood, int numAverage, int numMiss, int noteCount) {
        return noteCount == (numPerfect + numGood + numAverage + numMiss);
    }
    
    private static double getNewGoodCount(int numGood, int numAverage, int numMiss, int numBoo) {
        return (double)numGood + (double)numAverage * 1.8 + (double)numMiss * 2.4 + (double)numBoo * 0.2;
    }
    
    private static double getAAAEquivalent(double newGoodCount, double difficulty) {
        double alpha = 9.9750396740034;
        double beta = 0.0193296437339205;
        double lambda = 18206628.7286425;
        double delta =  17678803623.9633 + 
                733763392.922176 * difficulty + 
                28163834.4879901 * Math.pow(difficulty,2) - 
                434698.513947563 * Math.pow(difficulty,3) + 
                3060.24243867853 * Math.pow(difficulty,4);
        double AAAEq = Math.pow((delta - newGoodCount * lambda) / delta * Math.pow(difficulty + alpha, beta), 1 / beta) - alpha;
        return Math.max(0, AAAEq);
    }
}