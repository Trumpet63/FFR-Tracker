package src;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewManager {
    private static Stage PRIMARY_STAGE;
    
    public static void init(Stage stage) {
        PRIMARY_STAGE = stage;
    }
    
    public static void setScene(SceneOption scene) {
        switch(scene) {
            case CONFIG:
                Scene configScene = ConfigView.getScene(ConfigField.values());
                PRIMARY_STAGE.setScene(configScene);
                break;
            case SCORES:
                Scene scoresScene = ScoresView.getScene();
                PRIMARY_STAGE.setScene(scoresScene);
                break;
        }
    }
}
