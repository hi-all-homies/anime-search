package app.controllers;

import java.util.Arrays;
import java.util.List;
import app.config.ContainerHolder;
import static app.model.request.RequestType.*;
import app.model.anime.enums.Genre;
import app.model.request.SearchRequest;
import app.model.request.RequestType;
import app.util.DataTransferService;
import app.util.RequestPublisher;
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
import static app.config.Container.LIMIT;


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

    private RequestPublisher reqPublisher;


    private DataTransferService dataService;

    final PauseTransition pause = new PauseTransition(Duration.millis(2000));

    public void initialize() {
        var container = ContainerHolder.INSTANCE.getContainer();
        var viewInjector = container.getViewInjector();
        this.reqPublisher = container.getRequestPublisher();
        this.dataService = container.getDataTransferService();

        this.checkBoxPane.getChildren().addAll(this.initCheckBoxes());

        this.dataService.setGenreChoices(this.genreChoices);
        this.dataService.setSearchField(this.searchField);

        var animeList = viewInjector.load("/views/anime-list.fxml");
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
        this.reqPublisher.publishRequest(request);
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