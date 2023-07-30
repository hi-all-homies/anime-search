package app.model.request;

public record SearchRequest(RequestType type, int id, String query, int page, int limit, int[] genres) {

    public SearchRequest(int id){
        this(RequestType.SINGLE, id, null, 0, 0, null);
    }

    public SearchRequest(RequestType type){
        this(type, 0, null, 0, 0, null);
    }

}
