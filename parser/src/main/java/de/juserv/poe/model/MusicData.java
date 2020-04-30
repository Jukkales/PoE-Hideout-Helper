package de.juserv.poe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MusicData {

    private int id;
    @JsonProperty(index = 3)
    private Long hashId;
    @JsonProperty(index = 5)
    private String name;
    @JsonProperty(index = 4)
    private Boolean forHideout;
}
