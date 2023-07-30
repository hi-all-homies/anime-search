package app.model.response;

import java.util.Collection;

public interface Response<T> {
    Collection<T> getListedData();
    T getSingleData();
}