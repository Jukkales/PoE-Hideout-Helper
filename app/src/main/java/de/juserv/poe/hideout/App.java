package de.juserv.poe.hideout;

import de.juserv.poe.hideout.gui.HideoutSelector;
import de.juserv.poe.hideout.gui.Messages;
import org.jnativehook.GlobalScreen;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static Properties appProperties;

    public static String getProperty(String key) {
        if (appProperties == null) {
            loadProperties();
        }
        return appProperties.getProperty(key);
    }

    private static void loadProperties() {
        appProperties = new Properties();
        try {
            appProperties.load(App.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getString("message.errorMessage", e.getMessage()),
                    Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
        appProperties.put("updateCheck.done", "false");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            GlobalScreen.registerNativeHook();

            new HideoutSelector().doModal();
        } catch (Throwable e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, Messages.getString("message.errorMessage", e.getMessage()),
                    Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setProperty(String key, String value) {
        if (appProperties == null) {
            loadProperties();
        }
        appProperties.setProperty(key, value);
    }

}
