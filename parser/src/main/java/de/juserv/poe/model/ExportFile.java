package de.juserv.poe.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.juserv.poe.jackson.IndexDeserializer;
import java.util.List;
import lombok.Data;

@Data
public class ExportFile<T> {

    @JsonDeserialize(using = IndexDeserializer.class)
    private List<T> data;
    private String filename;

}
