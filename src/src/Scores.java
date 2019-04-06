package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

public class Scores {
    public static ObservableList<Score> SCORES = FXCollections.observableArrayList();
    private static File SCORES_FILE;
    
    public static void init(String scoresFilePath) {
        LogController.LOGGER.info("Initializing scores...");
        SCORES_FILE = new File(scoresFilePath);
        if(!SCORES_FILE.exists())
            initializeScoresFile();
        int lineCount = countLinesInFile(SCORES_FILE);
        LogController.LOGGER.info(lineCount + " scores found...");
        loadScoresFile(lineCount);
        LogController.LOGGER.info("Scores initialized!");
    }
    
    private static void loadScoresFile(int lineCount){
        try (BufferedReader br = new BufferedReader(new FileReader(SCORES_FILE));) {
            if(lineCount <= Config.MAX_SCORES_TO_LOAD)
                loadWholeScoresFile(br);
            else
                loadScoresFromLineNumber(lineCount - Config.MAX_SCORES_TO_LOAD, br);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void loadScoresFromLineNumber(int startLine, BufferedReader br) throws IOException {
        LogController.LOGGER.info("Loading scores from line " + startLine + "...");
        String line;
        for(int i = 0; i < startLine; i++)
            br.readLine();
        int lineNumber = startLine;
        while((line = br.readLine()) != null) {
            try {
                SCORES.add(0, new Score(line));
            }
            catch(NumberFormatException ex) {
                handleInvalidScore(ex, lineNumber);
            }
            lineNumber++;
        }
    }
    
    private static void loadWholeScoresFile(BufferedReader br) throws IOException {
        LogController.LOGGER.info("Loading whole scores file...");
        String line;
        int lineNumber = 0;
        while((line = br.readLine()) != null) {
            try {
                SCORES.add(0, new Score(line));
            }
            catch(NumberFormatException ex) {
                handleInvalidScore(ex, lineNumber);
            }
            lineNumber++;
        }
    }
    
    private static void initializeScoresFile() {
        LogController.LOGGER.info("Initializing scores file...");
        try {
            SCORES_FILE.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static int countLinesInFile(File file){
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file));) {
            while(br.readLine() != null)
                lineCount++;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lineCount;
    }
    
    public static void storeNewScores(JSONArray games) {
        List<Score> newScores = addNewScores(games);
        LogController.LOGGER.info("Found " + newScores.size() + " new scores");
        if(newScores.size() > 0) {
            appendNewScoresToFile(newScores);
            updateBestAAAEquivalent(newScores);
        }
    }
    
    private static List<Score> addNewScores(JSONArray games) {
        List<Score> newScores = new ArrayList<>();
        for(int i = games.length() - 1; i >= 0 ; i--) {
            JSONObject game = games.getJSONObject(i);
            Score score = new Score(game);
            if(!SCORES.contains(score)) {
                SCORES.add(0, score);
                newScores.add(score);
            }
        }
        return newScores;
    }
    
    /**
     * This function appends to the end of the scores file. Scores are stored in
     * ascending order of timestamp.
     * @param newScores New scores to be appended. Assumed to be given in
     * ascending order of timestamp.
     */
    private static void appendNewScoresToFile(List<Score> newScores) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SCORES_FILE, true));) {
            for(int i = 0; i < newScores.size(); i++) {
                bw.write(newScores.get(i) + "\n");
            }
        } catch (FileNotFoundException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    // lineNumber starts at 0
    private static void handleInvalidScore(NumberFormatException ex, int lineNumber) {
        LogController.LOGGER.info("Unable to load score on line " + lineNumber + ". Reason: " + ex.toString());
    }

    private static void updateBestAAAEquivalent(List<Score> newScores) {
        boolean changed = false;
        double bestAAAEq = Config.BEST_AAA_EQUIVALENT;
        for(Score score: newScores) {    
            double aaaEq = score.getAAAEquivalent();
            if(aaaEq > bestAAAEq) {
                changed = true;
                bestAAAEq = aaaEq;
            }
        }
        if(changed) {
            ScoresView.triggerNewBestAAAEquivalentNotification(bestAAAEq);
            sendBestAAAEquivalentToConfig(bestAAAEq);
        }
    }

    private static void sendBestAAAEquivalentToConfig(double bestAAAEq) {
        Map<ConfigField, String> aaaEquivalentMap = new HashMap<>();
        aaaEquivalentMap.put(ConfigField.BEST_AAA_EQUIVALENT, Double.toString(bestAAAEq));
        Config.update(aaaEquivalentMap);
    }
}