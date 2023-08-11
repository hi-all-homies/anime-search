package app.model.relations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Entry(
        @JsonProperty("mal_id") int id,
        String type, String name) {}