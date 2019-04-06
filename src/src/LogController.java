package src;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogController {
    public static final Logger LOGGER = Logger.getLogger(Console.TITLE.replaceAll(" ", "_"));
    private static final String STARTUP_MESSAGE = "Running: %s, Version: %s";
    
    public static void init() {
        try {
            FileHandler handler = new FileHandler("log.log");
            LOGGER.addHandler(handler);
            handler.setFormatter(new SimpleFormatter());
            //LOGGER.setUseParentHandlers(false);
        } catch (IOException | SecurityException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        LOGGER.info(String.format(STARTUP_MESSAGE, Console.TITLE, Console.VERSION));
    }
}
