package app.model.anime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Trailer(
        String url,
        @JsonProperty("embed_url") String embedUrl) implements Serializable {}