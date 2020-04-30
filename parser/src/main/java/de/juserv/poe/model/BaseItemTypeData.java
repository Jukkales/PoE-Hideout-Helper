package de.juserv.poe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseItemTypeData {

    @JsonProperty(index = 19)
    private Long hashId;
    private int id;
    @JsonProperty(index = 4)
    private String name;
}
