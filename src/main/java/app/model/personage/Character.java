package app.model.personage;

import app.model.img.Images;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Character(
        @JsonProperty("mal_id") int id,
        String name, Images images) {}