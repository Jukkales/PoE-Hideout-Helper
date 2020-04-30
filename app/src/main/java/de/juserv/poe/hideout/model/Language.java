package de.juserv.poe.hideout.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PoEs Languages
 */
@Getter
@AllArgsConstructor
public enum Language {

    ENGLISH("English", "us"),
    GERMAN("German", "de"),
    PORTUGUESE("Portuguese", "br"),
    RUSSIAN("Russian", "ru"),
    THAI("Thai", "th"),
    KOREAN("Korean", "kr"),
    SPANISH("Spanish", "sp"),
    FRENCH("French", "fr");

    private final String languageName;
    private final String localCode;

    @Override
    public String toString() {
        return languageName;
    }
}
