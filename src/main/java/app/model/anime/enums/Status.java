package app.model.anime.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("Currently Airing")
    AIRING,

    @JsonProperty("Not yet aired")
    UPCOMING,

    @JsonProperty("Finished Airing")
    FINISHED,

    @JsonEnumDefaultValue
    UNKNOWN;
}