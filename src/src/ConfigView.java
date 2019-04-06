package src;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ConfigView {
    private static final double GRID_HGAP = 12;
    private static final double GRID_VGAP = 10;
    private static final Insets GRID_PADDING = new Insets(25, 25, 25, 25);
    private static final String TITLE = "Config Menu";
    private static final Font TITLE_FONT = Font.font("Tahoma", FontWeight.NORMAL, 20);
    private static final String SUBMIT_BUTTON_TEXT = "Save Changes";
    private static final String RESET_BUTTON_TEXT = "Reset";
    private static final String CANCEL_BUTTON_TEXT = "Cancel";
    private static Label ALERT_MESSAGE;
    private static final int ALERT_TIMEOUT = 10000;
    
    public static Scene getScene(ConfigField[] configFields) {
        BorderPane pane = new BorderPane();
        initializeAlertMessage();
        pane.setTop(ALERT_MESSAGE);
        GridPane grid = getGrid();
        Text sceneTitle = getSceneTitle();
        grid.add(sceneTitle, 0, 0, 2, 1);
        Map<ConfigField, TextField> configTextFields = addTextFieldsForEach(grid, configFields);
        addBottomControls(pane, configTextFields);
        pane.setCenter(grid);
        return new Scene(pane, 500, 450);
    }
    
    private static void initializeAlertMessage() {
        ALERT_MESSAGE = new Label();
        ALERT_MESSAGE.setAlignment(Pos.CENTER);
        ALERT_MESSAGE.setPadding(new Insets(20, 4, 0, 4));
    }

    private static GridPane getGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(GRID_HGAP);
        grid.setVgap(GRID_VGAP);
        grid.setPadding(GRID_PADDING);
        return grid;
    }

    private static Text getSceneTitle() {
        Text sceneTitle = new Text(TITLE);
        sceneTitle.setFont(TITLE_FONT);
        return sceneTitle;
    }

    private static Map<ConfigField, TextField> addTextFieldsForEach(GridPane grid, ConfigField[] configFields) {
        Map<ConfigField, TextField> configTextFields = new HashMap<>();
        for(int i = 0; i < configFields.length; i++) {
            String fieldName = configFields[i].getFieldName();
            Label label = new Label(fieldName);
            grid.add(label, 0, i + 1);
            TextField textField;
            if(isSecretField(configFields[i]))
                textField = new PasswordField();
            else
                textField = new TextField();
            textField.setText(getConfigValue(configFields[i]));
            grid.add(textField, 1, i + 1);
            configTextFields.put(configFields[i], textField);
        }
        return configTextFields;
    }
    
    

    private static Map<ConfigField, String> getConfigAssignments(Map<ConfigField, TextField> configTextFields) {
        Map<ConfigField, String> configAssignments = new HashMap<>();
        configTextFields.forEach((fieldName, textField) -> {
            configAssignments.put(fieldName, textField.getText());
        });
        return configAssignments;
    }

    private static Button getSubmissionButton(Map<ConfigField, TextField> configTextFields) {
        Button btn = new Button(SUBMIT_BUTTON_TEXT);
        btn.setOnAction((ActionEvent event) -> {
            Map<ConfigField, String> configAssignments = getConfigAssignments(configTextFields);
            if(validateConfigAssignments(configAssignments)) {
                Config.update(configAssignments);
                if(!SongList.initialized)
                    SongList.init();
                ViewManager.setScene(SceneOption.SCORES);
            }
        });
        return btn;
    }

    private static String getConfigValue(ConfigField configField) {
        switch(configField) {
            case API_KEY:
                return Config.API_KEY;
            case USERNAME:
                return Config.USERNAME;
            case MAX_SCORES_TO_LOAD:
                return Integer.toString(Config.MAX_SCORES_TO_LOAD);
            case DATE_FORMAT:
                return Config.DATE_FORMAT.toPattern();
            case BEST_AAA_EQUIVALENT:
                return Double.toString(Config.BEST_AAA_EQUIVALENT);
            default:
                return "";
        }
    }

    private static boolean isSecretField(ConfigField field) {
        return field.equals(ConfigField.API_KEY);
    }

    private static Button getCancelButton() {
        Button btn = new Button(CANCEL_BUTTON_TEXT);
        btn.setOnAction((ActionEvent event) -> {
            ViewManager.setScene(SceneOption.SCORES);
        });
        return btn;
    }
    
    private static Button getResetButton(Map<ConfigField, TextField> configTextFields) {
        Button btn = new Button(RESET_BUTTON_TEXT);
        btn.setOnAction((ActionEvent event) -> {
            configTextFields.forEach((configField, textField) -> {
                textField.setText(getConfigValue(configField));
            });
        });
        return btn;
    }

    private static void addBottomControls(BorderPane pane, Map<ConfigField, TextField> configTextFields) {
        Button cancelButton = getCancelButton();
        Button resetButton = getResetButton(configTextFields);
        Button submissionButton = getSubmissionButton(configTextFields);
        HBox hbox = new HBox();
        hbox.setSpacing(14);
        hbox.getChildren().addAll(cancelButton, resetButton, submissionButton);
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.setPadding(new Insets(0, 0, 40, 0));
        pane.setBottom(hbox);
    }

    private static boolean validateConfigAssignments(Map<ConfigField, String> configAssignments) {
        Iterator<Entry<ConfigField, String>> iter = configAssignments.entrySet().iterator();
        while(iter.hasNext()) {
            Entry<ConfigField, String> entry = iter.next();
            ConfigField configField = entry.getKey();
            String fieldContents = entry.getValue();
            boolean valid = validateField(configField, fieldContents);
            if(!valid)
                return false;
        }
        return true;
    }

    private static boolean validateField(ConfigField configField, String fieldContents) {
        try {
            switch(configField) {
                case MAX_SCORES_TO_LOAD:
                    try {
                        Integer.parseInt(fieldContents);
                    }
                    catch(NumberFormatException ex) {
                        setAlertMessage(getGenericError(configField) + "Must be an integer.");
                        return false;
                    }
                    return true;
                case DATE_FORMAT:
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(fieldContents);
                    }
                    catch(IllegalArgumentException ex) {
                        setAlertMessage(getGenericError(configField) + ex.getMessage());
                        return false;
                    }
                    return true;
                case BEST_AAA_EQUIVALENT:
                    try {
                        Double.parseDouble(fieldContents);
                    }
                    catch(NumberFormatException ex) {
                        setAlertMessage(getGenericError(configField) + "Must be valid decimal number.");
                        return false;
                    }
                    return true;
                case API_KEY:
                case USERNAME:
                    if(fieldContents == null || fieldContents.isEmpty()) {
                        setAlertMessage(getGenericError(configField) + "Must not be empty.");
                        return false;
                    }
                    return true;
            }
        }
        catch(Exception ex) {
            setAlertMessage(getGenericError(configField) + ex.toString());
            return false;
        }
        return false;
    }
    
    private static void setAlertMessage(String message) {
        ALERT_MESSAGE.setText(message);
        new Timer().schedule( 
            new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(
                        () -> { ALERT_MESSAGE.setText(""); }
                    );
                    this.cancel();
                }
            }, 
            ALERT_TIMEOUT
        );
    }
    
    private static String getGenericError(ConfigField field) {
        return "Error with field '" + field.getFieldName() + "': ";
    }
}
