package de.juserv.poe.hideout.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "hashId")
public class DoodadInfo implements Serializable {
    private static final long serialVersionUID = 8089074860601369021L;
    private Long hashId;
    private Long cost;
    private boolean isMTX = false;
    private Long level;
    private Long master;
    private Map<String, String> name = new HashMap<>();
}
