package app.model.anime.aired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DateRange(
        Day from, Day to) implements Serializable {}
