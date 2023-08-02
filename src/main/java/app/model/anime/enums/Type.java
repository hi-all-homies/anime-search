package app.model.anime.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("TV")
    TV("tv"),

    @JsonProperty("Movie")
    MOVIE("movie"),

    @JsonProperty("OVA")
    OVA("ova"),

    @JsonProperty("Special")
    SPECIAL("special"),

    @JsonProperty("ONA")
    ONA("ona"),

    @JsonProperty("Music")
    MUSIC("music"),

    @JsonEnumDefaultValue
    ALL("all");

    public final String name;

    Type(String name) {
        this.name = name;
    }
}
