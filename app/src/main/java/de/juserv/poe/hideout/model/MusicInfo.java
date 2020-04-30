package de.juserv.poe.hideout.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class MusicInfo implements Serializable {
    private static final long serialVersionUID = 4297792931670360077L;
    private Long hashId;
    private Map<String, String> name = new HashMap<>();
}
