package app.controllers;


import app.model.anime.Anime;
import app.model.anime.GenreEntity;
import app.model.personage.Personage;
import app.model.request.SearchRequest;
import app.service.anime.AnimeService;
import app.service.injector.ViewInjector;
import app.util.DataTransfer;
import app.util.ViewValueExtractor;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class SingleAnimeController {

    public ImageView img;
    public Label type;
    public Label source;
    public Label score;
    public Label year;
    public Label rating;
    public Label duration;
    public Label episodes;
    public Label status;
    public Label aired;
    public Label studio;
    public Label genres;
    public Pagination characters;
    public TextFlow synopsis;
    public TextFlow background;
    public Button like;
    public VBox openings;
    public VBox endings;
    public TilePane relations;

    public static final String ADD_LIKE = "like";
    public static final String REMOVE_LIKE = "dislike";
    public Button trailer;
    public FontIcon likeIcon;

    private Anime anime;

    private final DataTransfer dataService;

    private final AnimeService animeService;

    private final ViewInjector viewInjector;

    private final ViewValueExtractor extractor;

    private final List<Personage> characterList = new ArrayList<>();

    private final Subject<String> closeEmitter;

    private final Map<Integer, Anime> likedAnime;

    private final Subject<SearchRequest> reqPublisher;


    public SingleAnimeController(DataTransfer dataService, AnimeService animeService, ViewInjector viewInjector, Map<Integer, Anime> likedAnime, Subject<String> closeEmitter, ViewValueExtractor extractor, Subject<SearchRequest> reqPublisher) {
        this.dataService = dataService;
        this.animeService = animeService;
        this.viewInjector = viewInjector;
        this.extractor = extractor;
        this.closeEmitter = closeEmitter;
        this.likedAnime = likedAnime;
        this.reqPublisher = reqPublisher;
    }

    public void initialize(){
        this.setFields();
        this.setCharacters();
    }


    public void submitLike() {

        if (this.likedAnime.containsKey(this.anime.id())){
            this.likedAnime.remove(this.anime.id());
            this.like.setText(ADD_LIKE);
            this.likeIcon.setIconLiteral("far-heart");
        }
        else {
            var liked = new Anime(this.anime, this.extractor.getTitle(anime), this.extractor.getYear(anime));
            this.likedAnime.put(liked.id(), liked);
            this.like.setText(REMOVE_LIKE);
            this.likeIcon.setIconLiteral("fas-heart");
        }
    }

    public void openTrailer() {
        this.trailer.setDisable(true);

        var node = this.viewInjector.load("/views/trailer.fxml");
        var bounds = Screen.getPrimary().getBounds();

        var scene = new Scene(node, bounds.getWidth()*50/100, bounds.getHeight()*50/100);

        var trailerStage = new Stage();
        trailerStage.setTitle(anime.title());
        trailerStage.getIcons().add(new Image("/assets/icon.png"));
        trailerStage.setScene(scene);

        trailerStage.setOnCloseRequest(event -> {
            this.closeEmitter.onNext(anime.title());
            this.trailer.setDisable(false);
        });

        trailerStage.show();
    }


    private void setFields(){
        this.anime = this.dataService.getAnime();

        this.img.setImage(new Image(anime.images().jpg().largeImageUrl()));

        this.type.setText(String.format("Type: %s", anime.type() != null ? anime.type().name : " - "));
        this.source.setText(String.format("Source: %s", anime.source() != null ? anime.source() : " - "));
        this.score.setText(String.format("Score: %.2f", anime.score()));

        this.year.setText(this.extractor.getYearString(this.anime));

        this.rating.setText(this.extractor.getRating(this.anime));

        this.duration.setText(String.format("Duration: %s", anime.duration() != null ? anime.duration() : " - "));
        this.episodes.setText(this.extractor.getEpisodes(this.anime));


        this.status.setText("Status: " + anime.status().name);
        this.status.setStyle(this.extractor.getStatusStyle(this.anime));

        this.aired.setText(String.format("Aired from %s",
                anime.aired().range() != null ? anime.aired().range() : "?"));


        var studio = anime.studios().stream().findFirst();
        this.studio.setText(String.format("Studio: %s", studio.isPresent() ? studio.get().name() : " - "));


        var stringGenres = Stream.concat(anime.genres().stream(), anime.themes().stream())
                .map(GenreEntity::name)
                .map(String::toLowerCase)
                .reduce("Genres: ", (res, el) -> res + el + "; ");
        this.genres.setText(stringGenres);

        var background = new Text(anime.background() != null ? anime.background() : "none");

        this.synopsis.getChildren().add(new Text(this.extractor.getSynopsis(this.anime)));
        this.background.getChildren().add(background);

        boolean isLiked = this.likedAnime.containsKey(this.anime.id());
        this.like.setText(isLiked ? REMOVE_LIKE : ADD_LIKE);
        this.likeIcon.setIconLiteral(isLiked ? "fas-heart" : "far-heart");

        this.trailer.setVisible(this.anime.trailer().embedUrl() != null);

        var ops = this.openings.getChildren();
        ops.add(new Label("Openings:"));
        var ends = this.endings.getChildren();
        ends.add(new Label("Endings:"));

        ops.addAll(this.createSongs(this.anime.theme().openings()));
        ends.addAll(this.createSongs(this.anime.theme().endings()));


        anime.relations().stream()
                .flatMap(relation -> relation.entry().stream())
                .map(entry -> {
                    var entryBox = new VBox();
                    entryBox.setSpacing(10);
                    entryBox.setAlignment(Pos.TOP_CENTER);
                    entryBox.setPadding(new Insets(5d));
                    entryBox.getStyleClass().add("background-list");

                    var name = new Label(entry.name());
                    name.getStyleClass().add("related-name");
                    var type = new Label("type: " + entry.type());

                    if (entry.type().equals("anime")){
                        name.setCursor(Cursor.HAND);
                        name.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            if (event.getButton() == MouseButton.PRIMARY)
                                this.reqPublisher.onNext(new SearchRequest(entry.id()));
                        });
                    }

                    entryBox.getChildren().addAll(type, name);
                    return entryBox;
                })
                .forEach(this.relations.getChildren()::add);
    }

    private List<TextField> createSongs(List<String> songs){
        return songs.stream()
                .map(value -> {
                    var songField = new TextField(value);
                    songField.setEditable(false);
                    songField.getStyleClass().add("copyable-field");
                    return songField;
                })
                .toList();
    }


    private void setCharacters(){
        this.animeService.findPersonageById(this.anime.id())
                .doOnComplete(this::buildPage)
                .subscribe(this.characterList::add);
    }

    private final int itemsPerPage = 5;

    private void buildPage(){
        Platform.runLater(() -> {
            var evenOdd = this.characterList.size() % this.itemsPerPage;
            var count = this.characterList.size() / this.itemsPerPage;
            this.characters.setPageCount(evenOdd == 0 ? count : count + 1);

            this.characters.setPageFactory(this::getPageFactory);
        });
    }

    private Node getPageFactory(Integer index){
        int from = index * this.itemsPerPage;
        int to = Math.min(from + this.itemsPerPage, this.characterList.size());

        final HBox hBox = new HBox();
        hBox.setSpacing(10d);
        hBox.setFillHeight(true);
        hBox.setAlignment(Pos.TOP_CENTER);

        var wrapper = new ScrollPane(hBox);
        wrapper.setFitToHeight(true);
        wrapper.setFitToWidth(true);
        wrapper.setFocusTraversable(false);
        wrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        wrapper.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        wrapper.getStyleClass().add("edge-to-edge");

        Observable.fromIterable(this.characterList.subList(from, to))
                .subscribeOn(Schedulers.single())
                .doOnNext(this.dataService::setPersonage)
                .map(personage -> this.viewInjector.load("/views/character-image.fxml"))
                .subscribe(node ->
                        Platform.runLater(() -> hBox.getChildren().add(node)));

        return wrapper;
    }
}