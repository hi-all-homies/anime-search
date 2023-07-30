package app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseSingle<T> (T data) implements Response<T> {
    @Override
    public Collection<T> getListedData() {
        return null;
    }

    @Override
    public T getSingleData() {
        return this.data;
    }
}