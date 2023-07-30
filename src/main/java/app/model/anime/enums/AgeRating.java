package app.model.anime.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum AgeRating{
    @JsonProperty("G - All Ages")
    G("g", "G - All Ages"),

    @JsonProperty("PG - Children")
    PG("pg", "PG - Children"),

    @JsonProperty("PG-13 - Teens 13 or older")
    PG13("pg13", "PG-13 - Teens 13 or older"),

    @JsonProperty("R - 17+ (violence & profanity)")
    R17("r17", "R - 17+ (violence & profanity)"),

    @JsonProperty("R+ - Mild Nudity")
    R("r", "R+ - Mild Nudity"),

    @JsonProperty("Rx - Hentai")
    RX("rx", "Rx - Hentai"),

    @JsonEnumDefaultValue
    UNKNOWN("unknown", "unknown");

    public final String type;
    public final String description;

    AgeRating(String type, String description) {
        this.type = type;
        this.description = description;
    }
}