package app.controllers;

import java.util.Arrays;
import java.util.List;
import static app.model.request.RequestType.*;
import app.model.anime.enums.Genre;
import app.model.request.SearchRequest;
import app.model.request.RequestType;
import app.service.injector.ViewInjector;
import app.util.DataTransferService;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;


public class MainController {

    public TextField searchField;
    public BorderPane contentHolder;

    public Button searchByGenres;
    public Button favorites;
    public Button ongoings;
    public Button topRated;

    public TitledPane genresPane;
    public TilePane checkBoxPane;
    private final ObservableMap<Integer, CheckBox> genreChoices = FXCollections.observableHashMap();

    private final Subject<SearchRequest> reqPublisher;
    private final ViewInjector viewInjector;
    private final DataTransferService dataService;

    public static final int LIMIT = 7;
    final PauseTransition pause = new PauseTransition(Duration.millis(2000));


    public MainController(Subject<SearchRequest> reqPublisher, ViewInjector viewInjector, DataTransferService dataService) {
        this.reqPublisher = reqPublisher;
        this.viewInjector = viewInjector;
        this.dataService = dataService;
    }

    public void initialize() {
        this.checkBoxPane.getChildren().addAll(this.initCheckBoxes());

        this.dataService.setGenreChoices(this.genreChoices);
        this.dataService.setSearchField(this.searchField);

        var animeList = this.viewInjector.load("/views/anime-list.fxml");
        this.contentHolder.setCenter(animeList);

        this.pause.setOnFinished(event -> Platform.runLater(() -> this.prepareToEmmit(SEARCH)));

        this.searchField.textProperty().addListener((obs, oldVal, nVal) -> {
            if (nVal.matches("^\\w\\w+[\\w\\s.]+"))
                this.pause.playFromStart();
        });

        this.emmitRequest();
    }


    public void submitSearch(){
        this.prepareToEmmit(SEARCH);
    }


    public void submitOngoings() {
        this.prepareToEmmit(NOW);
    }


    public void submitTopRated() {
        this.prepareToEmmit(TOP);
    }


    public void submitFavorites() {
        if (!this.dataService.getRequestType().equals(LIKED)){
            this.dataService.setReqHistory(this.dataService.getRequestType());
            this.dataService.setRequestType(LIKED);
            this.genresPane.setExpanded(false);
            this.emmitRequest();
        }
    }

    private void prepareToEmmit(RequestType type){
        this.dataService.setRequestType(type);
        this.dataService.getPage().set(1);
        this.genresPane.setExpanded(false);
        this.emmitRequest();
    }

    private void emmitRequest() {
        var ids = this.dataService.getSelectedGenres();
        var request = new SearchRequest(this.dataService.getRequestType(), 0,
                this.searchField.getText(), this.dataService.getPage().get(), LIMIT, ids);
        this.reqPublisher.onNext(request);
    }


    private List<CheckBox> initCheckBoxes() {
        return Arrays.stream(Genre.values())
                .map(genre -> {
                    CheckBox check = new CheckBox(genre.name);
                    check.setUserData(genre.id);
                    check.setOnAction(handleSelected);
                    return check;})
                .toList();
    }

    private final EventHandler<ActionEvent> handleSelected = event -> {
        var checkBox = (CheckBox) event.getSource();
        Integer boxId = (Integer)checkBox.getUserData();
        if (checkBox.isSelected())
            this.genreChoices.put(boxId, checkBox);
        else
            this.genreChoices.remove(boxId);
    };
}