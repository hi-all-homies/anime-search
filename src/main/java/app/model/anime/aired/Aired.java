package app.model.anime.aired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Aired(
        @JsonProperty("string") String range,
        DateRange prop) implements Serializable {}
