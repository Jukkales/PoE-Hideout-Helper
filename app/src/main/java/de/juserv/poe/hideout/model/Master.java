package de.juserv.poe.hideout.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Master {
    NON_MASTER(0L, "Non-Master"),
    NIKO(4L, "Niko"),
    ALVA(2L, "Alva"),
    EINHAR(1L, "Einhar"),
    JUN(5L, "Jun"),
    ZANA(6L, "Zana");

    private Long masterId;
    private String name;

    public static Master byId(Long id) {
        for (Master v : values()) {
            if (v.masterId.equals(id)) {
                return v;
            }
        }

        return null;
    }

    public Master next() {
        int pos = ordinal();
        if ( pos == values().length-1) {
            return values()[0];
        } else {
            return values()[pos+1];
        }
    }

    public Master prev() {
        int pos = ordinal();
        if (pos == 0) {
            return values()[values().length-1];
        } else {
            return values()[pos-1];
        }
    }
}
