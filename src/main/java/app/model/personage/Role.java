package app.model.personage;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Role {
    @JsonProperty("Main")
    MAIN,
    @JsonProperty("Supporting")
    SUPPORTING
}