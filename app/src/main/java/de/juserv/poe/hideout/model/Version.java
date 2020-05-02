package de.juserv.poe.hideout.model;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class Version {

    private static Pattern PATTERN_VERSION = Pattern.compile("(\\d+).(\\d+).?(\\d+)?");
    private int major;
    private int minor;
    private int patchLevel;
    private String versionString;

    public Version(String version) {
        versionString = version;
        Matcher m = PATTERN_VERSION.matcher(version);
        if (m.find()) {
            major = Integer.parseInt(m.group(1));
            minor = Integer.parseInt(m.group(2));
            if (m.groupCount() > 2 && m.group(3) != null) {
                patchLevel = Integer.parseInt(m.group(3));
            } else {
                patchLevel = 0;
            }
        } else {
            major = 0;
            minor = 0;
            patchLevel = 0;
        }
    }

    public boolean isNewer(Version other) {
        if (major > other.major) {
            return true;
        }
        if (major >= other.major && minor > other.minor) {
            return true;
        }
        return major >= other.major && minor >= other.minor && patchLevel > other.patchLevel;
    }
}
