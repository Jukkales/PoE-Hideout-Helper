package de.juserv.poe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WorldAreasData {

    @JsonProperty(index = 7)
    private Long hashId;
    @JsonProperty(index = 1)
    private String name;
    @JsonProperty(index = 42)
    private Boolean hideout;
}
