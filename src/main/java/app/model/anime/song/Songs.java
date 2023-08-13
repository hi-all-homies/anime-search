package app.model.anime.song;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Songs(List<String> openings, List<String> endings) implements Serializable {}