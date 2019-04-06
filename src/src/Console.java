package src;

import javafx.application.Application;
import javafx.stage.Stage;

public class Console extends Application {
    public static final String TITLE = "FFR Tracker";
    public static final String VERSION = "Alpha v0.1";
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(TITLE + " (" + VERSION + ")");
        LogController.init();
        ViewManager.init(primaryStage);
        FileManager.init();
        if(Config.allFieldsSet()) {
            ViewManager.setScene(SceneOption.SCORES);
            SongList.init();
        }
        else 
            ViewManager.setScene(SceneOption.CONFIG);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
