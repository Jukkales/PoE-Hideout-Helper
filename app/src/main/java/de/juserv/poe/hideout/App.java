package de.juserv.poe.hideout;

import de.juserv.poe.hideout.gui.HideoutSelector;
import de.juserv.poe.hideout.gui.Messages;
import org.jnativehook.GlobalScreen;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            GlobalScreen.registerNativeHook();

            new HideoutSelector().doModal();
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, Messages.getString("message.errorMessage", e.getMessage()),
                    Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
