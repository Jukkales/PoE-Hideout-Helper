package de.juserv.poe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HideoutDoodadsData {

    @JsonProperty(index = 0)
    private Long baseItemId;
    @JsonProperty(index = 2)
    private Long cost;
    private int id;
    @JsonProperty(index = 5)
    private Boolean isMasterDoodad;
    @JsonProperty(index = 3)
    private Long level;
    @JsonProperty(index = 4)
    private Long master;
}
