package app.model.anime.aired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Day(Integer day, Integer month, Integer year) implements Serializable {}
