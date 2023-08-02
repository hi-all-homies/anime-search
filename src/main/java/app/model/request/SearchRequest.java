package app.model.request;

import app.model.anime.enums.AgeRating;
import app.model.anime.enums.Status;
import app.model.anime.enums.Type;

public record SearchRequest(
        RequestType type, int id, String query,
        int page, int limit, int[] genres,Type typeChoice,
        AgeRating ageChoice, Status statusChoice, String minScore) {

    public SearchRequest(int id){
        this(RequestType.SINGLE, id, null, 0, 0, null, null, null, null, null);
    }

    public SearchRequest(RequestType type){
        this(type, 0, null, 0, 0, null, null, null, null, null);
    }

}
