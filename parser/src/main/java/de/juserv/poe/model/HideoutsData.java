package de.juserv.poe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HideoutsData {
    @JsonProperty(index = 2)
    private Long hashId;
    @JsonProperty(index = 8)
    private Boolean enabled;
}
