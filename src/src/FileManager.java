package src;

public class FileManager {
    private static final String CONFIG_LOCATION = "config.cfg";
    private static final String SCORES_LOCATION = "scores.csv";
    
    public static void init() {
        Config.init(CONFIG_LOCATION);
        Scores.init(SCORES_LOCATION);
    }
}
