package app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static app.model.request.RequestType.*;
import app.model.anime.enums.AgeRating;
import app.model.anime.enums.Genre;
import app.model.anime.enums.Status;
import app.model.anime.enums.Type;
import app.model.request.SearchRequest;
import app.model.request.RequestType;
import app.service.injector.ViewInjector;
import app.util.DataTransfer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.Subject;
import io.reactivex.rxjava3.subjects.UnicastSubject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.util.StringConverter;


public class MainController {

    public TextField searchField;
    private final Subject<String> searchFieldEvents = UnicastSubject.create();

    public BorderPane contentHolder;

    public Tab genresTab;
    public Tab topTab;
    public Tab ongoingTab;


    public TitledPane filtersPane;
    public TilePane genresPane;
    private final ObservableMap<Integer, CheckBox> genreChoices = FXCollections.observableHashMap();

    private final Subject<SearchRequest> reqPublisher;
    private final ViewInjector viewInjector;
    private final DataTransfer dataService;

    public static final int LIMIT = 7;

    public ChoiceBox<Type> typeChoice;
    public ChoiceBox<AgeRating> ageChoice;
    public ChoiceBox<Status> statusChoice;
    private final ObservableList<Status> searchStatuses = FXCollections.observableArrayList(
            Status.All, Status.AIRING, Status.UPCOMING, Status.COMPLETE);
    public Spinner<String> scoreSpinner;



    public MainController(Subject<SearchRequest> reqPublisher, ViewInjector viewInjector, DataTransfer dataService) {
        this.reqPublisher = reqPublisher;
        this.viewInjector = viewInjector;
        this.dataService = dataService;
    }

    public void initialize() {
        this.initFields();

        this.dataService.setGenreChoices(this.genreChoices);
        this.dataService.setSearchField(this.searchField);
        this.dataService.setTypeChoice(this.typeChoice);
        this.dataService.setAgeChoice(this.ageChoice);
        this.dataService.setStatusChoice(this.statusChoice);
        this.dataService.setScoreSpinner(this.scoreSpinner);

        var animeList = this.viewInjector.load("/views/anime-list.fxml");
        this.contentHolder.setCenter(animeList);

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
            this.filtersPane.setExpanded(false);
            this.emmitRequest();
        }
    }

    private void prepareToEmmit(RequestType type){
        this.dataService.setRequestType(type);
        this.dataService.getPage().set(1);
        this.filtersPane.setExpanded(false);
        this.emmitRequest();
    }

    private void emmitRequest() {
        this.reqPublisher.onNext(this.dataService.createRequest());
    }


    private void initFields(){
        this.initCheckBoxes();
        this.initFilterChoices();
        this.initScoreSpinner();

        this.searchFieldEvents.subscribeOn(Schedulers.single())
                .filter(value -> value.matches("^\\w\\w+[\\w\\s.]+"))
                .debounce(2000, TimeUnit.MILLISECONDS)
                .subscribe(value -> Platform.runLater(() -> this.prepareToEmmit(SEARCH)));

        this.searchField.textProperty()
                .addListener((obs, oldVal, nVal) -> this.searchFieldEvents.onNext(nVal));

        this.genresTab.selectedProperty().addListener((obs, oldVal, nVal) -> {
            if (nVal){
                this.statusChoice.setDisable(false);
                this.statusChoice.getItems().removeAll(Status.FAVORITE, Status.BY_POPULARITY);
                if (!this.searchStatuses.contains(Status.COMPLETE))
                    this.searchStatuses.add(Status.COMPLETE);
                this.statusChoice.setValue(Status.All);


                this.scoreSpinner.setDisable(false);

                this.ageChoice.setValue(AgeRating.All);
                this.ageChoice.setDisable(false);
                this.typeChoice.setValue(Type.ALL);
            }
        });

        this.topTab.selectedProperty().addListener((obs, oldVal, nVal) -> {
            if (nVal){
                this.statusChoice.setDisable(false);
                this.statusChoice.getItems().remove(Status.COMPLETE);
                if (!this.searchStatuses.contains(Status.FAVORITE))
                    this.statusChoice.getItems().addAll(Status.BY_POPULARITY, Status.FAVORITE);
                this.statusChoice.setValue(Status.All);

                this.scoreSpinner.getValueFactory().setValue("all");
                this.scoreSpinner.setDisable(true);

                this.ageChoice.setDisable(false);
                this.ageChoice.setValue(AgeRating.All);

                this.typeChoice.setValue(Type.ALL);
            }
        });

        this.ongoingTab.selectedProperty().addListener((obs, oldVal, nVal) -> {
            if (nVal){
                this.ageChoice.setValue(AgeRating.All);
                this.ageChoice.setDisable(true);

                this.statusChoice.setValue(Status.All);
                this.statusChoice.setDisable(true);

                this.scoreSpinner.getValueFactory().setValue("all");
                this.scoreSpinner.setDisable(true);

                this.typeChoice.setValue(Type.ALL);
            }
        });
    }

    private void initScoreSpinner(){
        var scoreValues = new ArrayList<String>();
        scoreValues.add("all");
        for (double start = 5, end = 9, step = 0.25; start <= end; start+= step)
            scoreValues.add(String.valueOf(start));

        var valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                FXCollections.observableArrayList(scoreValues));

        this.scoreSpinner.setValueFactory(valueFactory);
        this.scoreSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
    }

    public void resetFields() {
        this.genresPane.getChildren()
                .forEach(node -> ((CheckBox) node).setSelected(false));

        this.genreChoices.clear();

        this.typeChoice.setValue(Type.ALL);
        this.statusChoice.setValue(Status.All);
        this.ageChoice.setValue(AgeRating.All);
        this.scoreSpinner.getValueFactory().setValue("all");
    }

    private void initCheckBoxes() {
        var boxes = Arrays.stream(Genre.values())
                .map(genre -> {
                    CheckBox check = new CheckBox(genre.name);
                    check.setUserData(genre.id);
                    check.setOnAction(handleSelected);
                    return check;})
                .toList();
        this.genresPane.getChildren().addAll(boxes);
    }

    private final EventHandler<ActionEvent> handleSelected = event -> {
        var checkBox = (CheckBox) event.getSource();
        Integer boxId = (Integer)checkBox.getUserData();
        if (checkBox.isSelected())
            this.genreChoices.put(boxId, checkBox);
        else
            this.genreChoices.remove(boxId);
    };

    private void initFilterChoices(){
        this.typeChoice.setConverter(new TypeConverter());
        this.typeChoice.setValue(Type.ALL);
        for (Type type : Type.values())
            this.typeChoice.getItems().add(type);

        this.ageChoice.setConverter(new AgeConverter());
        this.ageChoice.setValue(AgeRating.All);
        for (AgeRating age : AgeRating.values())
            this.ageChoice.getItems().add(age);

        this.statusChoice.setConverter(new StatusConverter());
        this.statusChoice.setItems(this.searchStatuses);
        this.statusChoice.setValue(Status.All);
    }

    private static class TypeConverter extends StringConverter<Type>{
        @Override
        public String toString(Type object) {
            return object.name;
        }

        @Override
        public Type fromString(String string) {
            return Arrays.stream(Type.values())
                    .filter(type -> type.name.equals(string))
                    .findFirst()
                    .orElseThrow();
        }
    }

    private static class AgeConverter extends StringConverter<AgeRating>{
        @Override
        public String toString(AgeRating object) {
            return object.description;
        }

        @Override
        public AgeRating fromString(String string) {
            return Arrays.stream(AgeRating.values())
                    .filter(ageRating -> ageRating.description.equals(string))
                    .findFirst()
                    .orElseThrow();
        }
    }

    private static class StatusConverter extends StringConverter<Status>{
        @Override
        public String toString(Status object) {
            return object.description;
        }

        @Override
        public Status fromString(String string) {
            return Arrays.stream(Status.values())
                    .filter(status -> status.description.equals(string))
                    .findAny()
                    .orElseThrow();
        }
    }
}