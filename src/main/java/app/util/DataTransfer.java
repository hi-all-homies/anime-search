package app.util;

import app.model.DialogData;
import app.model.anime.Anime;
import app.model.anime.enums.AgeRating;
import app.model.anime.enums.Status;
import app.model.anime.enums.Type;
import app.model.personage.Personage;
import app.model.request.RequestType;
import app.model.request.SearchRequest;
import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import java.util.concurrent.atomic.AtomicInteger;
import static app.controllers.MainController.LIMIT;

public class DataTransfer {

    private RequestType requestType = RequestType.NOW;
    private RequestType reqHistory = RequestType.NOW;

    private ObservableMap<Integer, CheckBox> genreChoices;
    private AtomicInteger page;
    private TextField searchField;
    private ChoiceBox<Type> typeChoice;
    private ChoiceBox<AgeRating> ageChoice;
    private ChoiceBox<Status> statusChoice;
    private Spinner<String> scoreSpinner;

    private Anime anime;
    private Personage personage;

    private Throwable error;

    private DialogData dialogData;

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

    private int[] getSelectedGenres() {
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

    public void setTypeChoice(ChoiceBox<Type> typeChoice) {
        this.typeChoice = typeChoice;
    }

    public void setAgeChoice(ChoiceBox<AgeRating> ageChoice) {
        this.ageChoice = ageChoice;
    }

    public void setStatusChoice(ChoiceBox<Status> statusChoice) {
        this.statusChoice = statusChoice;
    }

    public void setScoreSpinner(Spinner<String> scoreSpinner) {
        this.scoreSpinner = scoreSpinner;
    }

    public SearchRequest createRequest(){
        var ids = getSelectedGenres();
        var type = this.typeChoice.getValue();
        var age = this.ageChoice.getValue();
        var status = this.statusChoice.getValue();
        var minScore = this.scoreSpinner.getValue();

        return new SearchRequest(
                this.requestType, 0, this.searchField.getText(),
                this.page.get(), LIMIT, ids, type, age, status, minScore);
    }

    public DialogData getDialogData() {
        return dialogData;
    }

    public void setDialogData(DialogData dialogData) {
        this.dialogData = dialogData;
    }
}