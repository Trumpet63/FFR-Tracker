package src;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;

public class ScoresView {
    private static Label NOTIFICATION = new Label();
    
    public static Scene getScene() {
        BorderPane contents = new BorderPane();
        TableView scoresTable = getTableView();
        HBox controlPanel = getControlPanel();
        VBox controlWithNotification = new VBox();
        controlWithNotification.setSpacing(15);
        NOTIFICATION.setPadding(new Insets(0, 4, 2, 4));
        controlWithNotification.getChildren().addAll(controlPanel, NOTIFICATION);
        contents.setCenter(scoresTable);
        contents.setTop(controlWithNotification);
        return new Scene(contents, 500, 450);
    }

    private static TableView getTableView() {
        TableView<Score> table = new TableView(Scores.SCORES);
        TableColumn<Score,String> nameCol = new TableColumn<>("Song");
        nameCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getName()));
        TableColumn<Score,String> AAAEqCol = new TableColumn<>("AAA Equivalent");
        AAAEqCol.setCellValueFactory(cdf -> Bindings.format("%.2f", cdf.getValue().getAAAEquivalent()));
        TableColumn<Score,String> dateCol = new TableColumn<>("Time");
        dateCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getDateString()));
        table.getColumns().setAll(dateCol, nameCol, AAAEqCol);
        return table;
    }

    private static HBox getControlPanel() {
        HBox controlPanel = new HBox();
        controlPanel.setSpacing(40);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(10, 10, 0, 10));
        
        Button configButton = new Button();
        configButton.setText("Config");
        configButton.setOnAction((ActionEvent event) -> {
            ViewManager.setScene(SceneOption.CONFIG);
        });
        
        Button fetchButton = new Button();
        fetchButton.setText("Fetch Recent Games");
        fetchButton.setOnAction((ActionEvent event) -> {
            if(!SongList.initialized)
                SongList.init();
            JSONArray recentGames = Crawler.getRecentGamesAndExtraInfo();
            Scores.storeNewScores(recentGames);
        });
        
        controlPanel.getChildren().add(configButton);
        controlPanel.getChildren().add(fetchButton);
        return controlPanel;
    }
    
    private static void triggerNotification(String message, int timeout) {
        NOTIFICATION.setText(message);
        new Timer().schedule( 
            new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(
                        () -> { NOTIFICATION.setText(""); }
                    );
                    this.cancel();
                }
            }, 
            timeout
        );
    }
    
    public static void triggerNewBestAAAEquivalentNotification(double newBest) {
        triggerNotification(String.format("New Best AAA Equivalent! (%.2f -> %.2f)", Config.BEST_AAA_EQUIVALENT, newBest), 10000);
    }
}
