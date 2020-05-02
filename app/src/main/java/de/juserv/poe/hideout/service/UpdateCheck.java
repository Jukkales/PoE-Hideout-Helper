package de.juserv.poe.hideout.service;

import de.juserv.poe.hideout.App;
import de.juserv.poe.hideout.gui.Messages;
import de.juserv.poe.hideout.model.Version;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which checks for new updates one time.
 */
public class UpdateCheck {

    private static final Pattern PATTERN_API_VERSION = Pattern.compile("\"name\"\\s?:\\s?\"(v(.+?))\"");
    private static Version current = new Version(App.getProperty("app.version"));

    public static void checkNewVersion() {
        if (!Boolean.parseBoolean(App.getProperty("updateCheck.done"))) {
            new Thread(() -> {
                Version last = getLastVersion();
                if (last.isNewer(current)) {
                    int result = JOptionPane
                            .showOptionDialog(null, Messages.getString("message.newVersion", last.getVersionString()),
                                    Messages.getString("dialog.titleUpdateAvailable"), JOptionPane.YES_NO_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            Desktop.getDesktop().browse(new URI(App.getProperty("app.baseUrl") + "/releases/latest"));
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e.getMessage(),
                                    Messages.getString("error"),
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }).start();
            App.setProperty("updateCheck.done", "true");
        }
    }

    private static Version getLastVersion() {
        try {
            URL url = new URL(App.getProperty("app.versionCheckUrl"));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            StringBuilder builder = new StringBuilder();
            while (br.ready()) {
                builder.append(br.readLine());
            }
            br.close();

            Matcher m = PATTERN_API_VERSION.matcher(builder.toString());

            if (m.find()) {
                return new Version((m.group(2)));
            } else {
                return current;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return current;
        }
    }
}
