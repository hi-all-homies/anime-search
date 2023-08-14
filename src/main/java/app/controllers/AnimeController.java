package app.controllers;

import app.Launcher;
import app.model.anime.Anime;
import app.model.request.SearchRequest;
import app.service.injector.ViewInjector;
import app.util.DataTransfer;
import app.util.ViewValueExtractor;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class AnimeController {
    public ImageView img;
    public Label title;
    public Label episodes;
    public Label status;
    public Label score;
    public Label year;
    public Label rating;
    public TextFlow textFlow;

    private Stage stage;

    private Anime anime;
    private final ViewInjector viewInjector;

    private final Subject<SearchRequest> publisher;

    private final DataTransfer dataService;

    private final ViewValueExtractor extractor;


    public AnimeController(ViewInjector viewInjector, Subject<SearchRequest> publisher, DataTransfer dataService, ViewValueExtractor extractor) {
        this.viewInjector = viewInjector;
        this.dataService = dataService;
        this.publisher = publisher;
        this.extractor = extractor;
    }

    public  void initialize(){
        this.anime = this.dataService.getAnime();

        this.setFields();

        this.img.setOnMouseEntered(this::cursorEntered);

        this.img.setOnMouseExited(event -> this.stage.close());
    }


    public void submitOnClick(MouseEvent mouseEvent){
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if (this.stage != null) this.stage.close();

            this.dataService.setReqHistory(this.dataService.getRequestType());

            this.publisher.onNext(new SearchRequest(this.anime.id()));
        }
    }


    private void setFields(){
        this.img.setImage(new Image(this.anime.images().jpg().imageUrl()));
        this.title.setText(this.extractor.getTitle(this.anime));

        this.status.setText("Status: " + anime.status().name);
        this.status.setStyle(this.extractor.getStatusStyle(this.anime));

        this.episodes.setText(this.extractor.getEpisodes(this.anime));

        this.rating.setText(this.extractor.getRating(this.anime));

        this.score.setText(String.format("Score: %.2f", anime.score()));

        this.year.setText(this.extractor.getYearString(this.anime));

        var synopsis = this.extractor.getSynopsis(this.anime);

        this.textFlow.getChildren().add(
                new Text(synopsis.length() > 350 ? synopsis.substring(0, 347) + "..." : synopsis));
    }


    private void cursorEntered (MouseEvent event){
        this.stage = new Stage();
        this.stage.initOwner(this.img.getScene().getWindow());
        this.stage.initStyle(StageStyle.UNDECORATED);
        var image = this.img.getImage();
        var imgHolder = this.viewInjector.load("/views/image-holder.fxml");
        var imgPlace = (ImageView)imgHolder.lookup("#image-holder");

        double height = image.getHeight();
        double width = image.getWidth();
        imgPlace.setImage(image);
        imgPlace.setFitHeight(height * 50 / 100 + height);
        imgPlace.setFitWidth(width * 50 / 100 + width);
        imgPlace.setPreserveRatio(true);

        Scene scene = new Scene(imgHolder);
        scene.getStylesheets().add(
                Launcher.DARK_MODE ? "/main.css" : "/light.css");

        this.stage.setScene(scene);
        this.stage.show();
    }
}