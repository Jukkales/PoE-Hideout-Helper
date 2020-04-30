package de.juserv.poe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PetData {
    @JsonProperty(index = 1)
    private Long baseItemId;
}
