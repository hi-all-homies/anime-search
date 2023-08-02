package app.model.anime.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("Currently Airing")
    AIRING("airing", "airing"),

    @JsonProperty("Not yet aired")
    UPCOMING("upcoming", "upcoming"),

    @JsonProperty("Finished Airing")
    COMPLETE("complete", "complete"),

    @JsonEnumDefaultValue
    All("all", "all"),

    BY_POPULARITY("bypopularity", "by popularity"),

    FAVORITE("favorite", "favorite");

    public final String name;

    public final String description;

    Status(String name, String description) {
        this.name = name;
        this.description = description;
    }
}