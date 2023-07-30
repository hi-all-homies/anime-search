package app.model.anime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Studio(
        @JsonProperty("mal_id") int id, String name) implements Serializable {}