package app.model.anime.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("TV")
    TV,

    @JsonProperty("Movie")
    MOVIE,

    @JsonProperty("OVA")
    OVA,

    @JsonProperty("Special")
    SPECIAL,

    @JsonProperty("ONA")
    ONA,

    @JsonProperty("Music")
    MUSIC,

    @JsonEnumDefaultValue
    ALL
}
