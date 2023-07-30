package app.controllers;

import static app.config.Container.LIMIT;
import app.config.ContainerHolder;
import app.exceptions.NotFoundException;
import app.injector.ViewInjector;
import app.model.anime.Anime;
import app.model.request.RequestType;
import app.model.request.SearchRequest;
import app.service.AnimeService;
import app.util.DataTransferService;
import app.util.RequestPublisher;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class AnimeListController {
    public VBox list;
    public AnimeService animeService;
    public BorderPane single;
    public ScrollPane listScroll;
    private final AtomicInteger page = new AtomicInteger(1);
    public ScrollPane singleScroll;

    public Label title;
    public Button backBtn;

    private ViewInjector viewInjector;
    private DataTransferService dataService;
    private RequestPublisher reqPublisher;


    public void initialize(){
        var container = ContainerHolder.INSTANCE.getContainer();
        this.single.setVisible(false);
        this.listScroll.visibleProperty().bind(this.single.visibleProperty().not());

        this.viewInjector = container.getViewInjector();
        this.reqPublisher = container.getRequestPublisher();
        this.animeService = container.getAnimeService();
        this.dataService = container.getDataTransferService();

        this.dataService.setPage(this.page);
        this.addScrollBarListener();
        this.reqPublisher.addObserver(filterRequests);
    }


    public void submitBack() {
        this.dataService.setRequestType(this.dataService.getReqHistory());
        this.reqPublisher.publishRequest(new SearchRequest(RequestType.HISTORY));
    }


    public static final String LISTED_ANIME = "/views/anime.fxml";
    public static final String SINGLE_ANIME = "/views/single-anime.fxml";
    public static final String FAVES = "/views/favorites.fxml";
    public static final String ERR_NODE = "/views/error-view.fxml";
    private final List<RequestType> reqMarks = List.of(RequestType.SEARCH, RequestType.TOP, RequestType.NOW);


    private final Consumer<SearchRequest> filterRequests = req -> {
        if (this.reqMarks.contains(req.type()) && req.page() <= 1)
            this.list.getChildren().clear();

        switch (req.type()) {
            case SEARCH -> this.animeService.findByQuery(req.query(), req.page(), req.limit(), req.genres())
                    .switchIfEmpty(data -> {
                        if (req.page() <= 1)
                            throw new NotFoundException("search query doesn't match any result");})
                    .doOnError(this::handleExceptions)
                    .subscribe(this::addToList);

            case TOP -> this.animeService.findTop(req.page(), req.limit())
                    .doOnError(this::handleExceptions)
                    .subscribe(this::addToList);

            case NOW -> this.animeService.findOngoings(req.page(), req.limit())
                    .doOnError(this::handleExceptions)
                    .subscribe(this::addToList);

            case SINGLE -> this.animeService.findById(req.id())
                    .doOnError(this::handleExceptions)
                    .subscribe(this::addToSingle);

            case LIKED -> this.addFavorites();

            case HISTORY -> this.single.setVisible(false);
        }
    };

    private void addToList(Anime anime){
        var node = this.prepareNode(LISTED_ANIME, anime);
        Platform.runLater(() -> {
            this.backBtn.setDisable(false);
            this.single.setVisible(false);
            this.list.getChildren().add(node);
        });
    }

    private void addToSingle(Anime anime){
        var node = this.prepareNode(SINGLE_ANIME, anime);

        Platform.runLater(() -> {
            this.backBtn.setDisable(false);
            this.title.setText(anime.titleEnglish() != null ? anime.titleEnglish() : anime.title());
            this.single.setVisible(true);
            this.singleScroll.setContent(node);
        });
    }

    private void addFavorites(){
        var node = this.viewInjector.load(FAVES);
        this.single.setVisible(true);
        this.title.setText("Liked anime & statistics");
        this.singleScroll.setContent(node);
    }


    private Parent prepareNode(String location, Anime anime){
        this.dataService.setAnime(anime);
        return this.viewInjector.load(location);
    }


    private void addScrollBarListener(){
        if (this.listScroll.getSkin() == null){
            ChangeListener<Skin<?>> skinChangeListener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Skin<?>> observableValue, Skin<?> skin, Skin<?> t1) {
                    listScroll.skinProperty().removeListener(this);
                    detectScrollEnd();
                }
            };
            this.listScroll.skinProperty().addListener(skinChangeListener);
        }
        else {
            detectScrollEnd();
        }
    }

    private void detectScrollEnd(){
        var scroll = (ScrollBar) this.listScroll.lookup(".scroll-bar:vertical");
        scroll.valueProperty().addListener((obs, ov, nv) -> {

            if ((double) nv == 1.0) {
                var currPage = this.page.incrementAndGet();
                var ids = this.dataService.getSelectedGenres();

                var request = new SearchRequest(this.dataService.getRequestType(), 0,
                        this.dataService.getSearchField().getText(), currPage, LIMIT, ids);

                this.reqPublisher.publishRequest(request);
            }
        });
    }

    private void handleExceptions (Throwable err) {
        this.dataService.setError(err);
        final var errNode = this.viewInjector.load(ERR_NODE);

        Platform.runLater(() -> {
            this.backBtn.setDisable(true);
            this.title.setText("");
            this.single.setVisible(true);
            this.singleScroll.setContent(errNode);
        });
    }
}