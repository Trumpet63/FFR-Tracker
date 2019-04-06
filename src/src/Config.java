package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    public static String API_KEY;
    public static String USERNAME;
    public static int MAX_SCORES_TO_LOAD = 100;
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mm aaa");
    public static double BEST_AAA_EQUIVALENT = 0;
    private static File CONFIG_FILE;
    
    public static void init(String configFilePath) {
        LogController.LOGGER.info("Checking for existing config file...");
        CONFIG_FILE = new File(configFilePath);
        if(!CONFIG_FILE.exists()) {
            LogController.LOGGER.info("Config file not found!");
            initializeConfigFile();
        }
        else {
            LogController.LOGGER.info("Config file found!");
            loadConfigFromFile();
        }
    }
    
    private static void loadConfigFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE));) {
            Map<ConfigField, String> configValues = readConfigFile(br);
            assignConfigValues(configValues);
            LogController.LOGGER.info("Config file loaded!");
        } catch (FileNotFoundException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private static void assignConfigValues(Map<ConfigField, String> configValues) {
        LogController.LOGGER.log(Level.INFO, "Loading config...\n{0}", configValues);
        if(configValues.containsKey(ConfigField.API_KEY)) {
            try {
                API_KEY = configValues.get(ConfigField.API_KEY);
            }
            catch(Exception ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        
        if(configValues.containsKey(ConfigField.USERNAME)) {
            try {
                USERNAME = configValues.get(ConfigField.USERNAME);
            }
            catch(Exception ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        
        if(configValues.containsKey(ConfigField.MAX_SCORES_TO_LOAD)) {
            try {
                MAX_SCORES_TO_LOAD = Integer.parseInt(configValues.get(ConfigField.MAX_SCORES_TO_LOAD));
            }
            catch(Exception ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        
        if(configValues.containsKey(ConfigField.DATE_FORMAT)) {
            try {
                DATE_FORMAT = new SimpleDateFormat(configValues.get(ConfigField.DATE_FORMAT));
            }
            catch(Exception ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        
        if(configValues.containsKey(ConfigField.BEST_AAA_EQUIVALENT)) {
            try {
                BEST_AAA_EQUIVALENT = Double.parseDouble(configValues.get(ConfigField.BEST_AAA_EQUIVALENT));
            }
            catch(Exception ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static Map<ConfigField, String> readConfigFile(BufferedReader br) throws IOException {
        Map<ConfigField, String> configValues = new HashMap<>();
        String line;
        while((line = br.readLine()) != null) {
            String[] pair = line.split("=");
            ConfigField field = ConfigField.get(pair[0].trim());
            if(field != null)
                configValues.put(field, pair[1].trim());
        }
        return configValues;
    }
    
    private static void initializeConfigFile() {
        BufferedWriter br = null;
        try {
            LogController.LOGGER.info("Creating new config file...");
            CONFIG_FILE.createNewFile();
            br = new BufferedWriter(new FileWriter(CONFIG_FILE));
            for(ConfigField field: ConfigField.values()) {
                br.write(field.getFieldName() + " = \n");
            }
            LogController.LOGGER.info("Config creation successful!");
        } 
        catch (IOException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void update(Map<ConfigField, String> configValues) {
        assignConfigValues(configValues);
        writeConfigToFile();
        LogController.LOGGER.info("Config updated!");
    }

    private static void writeConfigToFile() {
        LogController.LOGGER.info("Writing new config values to temporary file...");
        File newConfigFile = getNewFilledConfigFile();
        boolean deleteSuccess = CONFIG_FILE.delete();
        LogController.LOGGER.info("Delete old file... " + (deleteSuccess ? "success" : "failed"));
        boolean renameSuccess = newConfigFile.renameTo(CONFIG_FILE);
        LogController.LOGGER.info("Rename new file... " + (renameSuccess ? "success" : "failed"));
    }
    
    private static File getNewFilledConfigFile() {
        File newConfigFile = new File("new_config.cfg");
        BufferedWriter br = null;
        try {
            if(!newConfigFile.exists())
                newConfigFile.createNewFile();
            br = new BufferedWriter(new FileWriter(newConfigFile));
            String apiKeyString = API_KEY == null ? "" : API_KEY;
            br.write(ConfigField.API_KEY.getFieldName() + " = " + apiKeyString + "\n");
            String usernameString = USERNAME == null ? "" : USERNAME;
            br.write(ConfigField.USERNAME.getFieldName() + " = " + usernameString + "\n");
            br.write(ConfigField.MAX_SCORES_TO_LOAD.getFieldName() + " = " + MAX_SCORES_TO_LOAD + "\n");
            String dateFormatString = DATE_FORMAT == null ? "" : DATE_FORMAT.toPattern();
            br.write(ConfigField.DATE_FORMAT.getFieldName() + " = " + dateFormatString + "\n");
            br.write(ConfigField.BEST_AAA_EQUIVALENT.getFieldName() + " = " + BEST_AAA_EQUIVALENT + "\n");
            br.close();
            return newConfigFile;
        }
        catch (IOException ex) {
            LogController.LOGGER.log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ex) {
                LogController.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return newConfigFile;
    }
    
    public static boolean allFieldsSet() {
        return API_KEY != null && !API_KEY.isEmpty() &&
                USERNAME != null && !USERNAME.isEmpty() &&
                DATE_FORMAT != null;
    }
}
