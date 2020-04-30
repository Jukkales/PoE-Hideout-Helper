package de.juserv.poe.hideout.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class HideoutInfo implements Serializable {
    private static final long serialVersionUID = 4141186215021248267L;
    private Long hashId;
    private Map<String, String> name = new HashMap<>();
}
