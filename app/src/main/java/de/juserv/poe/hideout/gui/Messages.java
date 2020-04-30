package de.juserv.poe.hideout.gui;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Simple Class to access i18n keys.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Messages {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("lang/messages");

    /**
     * Translate the i18n key to the message for the current locale.
     *
     * @param message i18n-key
     * @return The message for the current locale.
     */
    public static String getString(String message) {
        try {
            return BUNDLE.getString(message);
        } catch (MissingResourceException e) {
            return '%' + message + '%';
        }
    }

    /**
     * Translate the i18n key to the message for the current locale while processing parameters.
     *
     * @param message i18n-key
     * @param params  parameters to replace
     * @return The message for the current locale.
     */
    public static String getString(String message, Object... params) {
        try {
            return MessageFormat.format(BUNDLE.getString(message), params);
        } catch (MissingResourceException e) {
            return '%' + message + '%';
        }
    }
}
