package app.util;

import app.model.anime.Anime;
import app.model.personage.Personage;
import app.model.request.RequestType;
import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import java.util.concurrent.atomic.AtomicInteger;

public class DataTransferService {

    private RequestType requestType = RequestType.NOW;
    private RequestType reqHistory = RequestType.NOW;

    private ObservableMap<Integer, CheckBox> genreChoices;
    private AtomicInteger page;
    private TextField searchField;

    private Anime anime;
    private Personage personage;

    private Throwable error;

    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestType getReqHistory() {
        return reqHistory;
    }

    public void setReqHistory(RequestType reqHistory) {
        this.reqHistory = reqHistory;
    }

    public void setGenreChoices(ObservableMap<Integer, CheckBox> genreChoices) {
        this.genreChoices = genreChoices;
    }

    public int[] getSelectedGenres() {
        return this.genreChoices.values()
                .stream()
                .mapToInt(checkBox -> (int) checkBox.getUserData())
                .toArray();
    }

    public AtomicInteger getPage() {
        return page;
    }

    public void setPage(AtomicInteger page) {
        this.page = page;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setSearchField(TextField searchField) {
        this.searchField = searchField;
    }

    public Personage getPersonage() {
        return personage;
    }

    public void setPersonage(Personage personage) {
        this.personage = personage;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
