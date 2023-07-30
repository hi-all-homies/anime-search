package app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseList<T> (Collection<T> data) implements Response<T> {
    @Override
    public Collection<T> getListedData() {
        return this.data;
    }

    @Override
    public T getSingleData() {
        return null;
    }
}