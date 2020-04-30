package de.juserv.poe.hideout.model;

import lombok.Data;

@Data
public class DoodadTableInfo {
    private Long amount;
    private String name;
    private Long cost;
    private Long level;
    private boolean isMTX;
    private boolean owned;
}
