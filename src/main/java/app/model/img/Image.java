package app.model.img;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Image(
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("large_image_url") String largeImageUrl) implements Serializable {}