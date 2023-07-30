package app.util;

import app.model.request.SearchRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RequestPublisher {

    private final List<Consumer<SearchRequest>> observers = new ArrayList<>();

    public void addObserver(Consumer<SearchRequest> observer){
        this.observers.add(observer);
    }

    public void publishRequest(final SearchRequest request){
        this.observers.forEach(observer -> observer.accept(request));
    }
}
