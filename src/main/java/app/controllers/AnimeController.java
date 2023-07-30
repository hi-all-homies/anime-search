package app.controllers;

import app.config.ContainerHolder;
import app.injector.ViewInjector;
import app.model.anime.Anime;
import app.model.anime.enums.Status;
import app.model.request.SearchRequest;
import app.util.DataTransferService;
import javafx.event.EventHandler;
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
    private ViewInjector viewInjector;

    private DataTransferService dataService;

    public  void initialize(){
        var container = ContainerHolder.INSTANCE.getContainer();
        this.viewInjector = container.getViewInjector();
        this.dataService = container.getDataTransferService();
        this.anime = this.dataService.getAnime();

        this.setFields();

        this.img.setOnMouseEntered(this.cursorEntered);

        this.img.setOnMouseExited(event -> this.stage.close());
    }


    public void submitOnClick(MouseEvent mouseEvent){
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if (this.stage != null) this.stage.close();

            this.dataService.setReqHistory(this.dataService.getRequestType());

            ContainerHolder.INSTANCE.getContainer()
                        .getRequestPublisher()
                        .publishRequest(new SearchRequest(this.anime.id()));
        }
    }


    private void setFields(){
        this.img.setImage(new Image(this.anime.images().jpg().imageUrl()));

        var titleEng = anime.titleEnglish();
        if (titleEng == null || "".equals(titleEng))
            this.title.setText(anime.title());
        else this.title.setText(titleEng);

        var animeStat = this.anime.status();
        this.status.setText("Status: " + animeStat.name());
        String style = String.format("-fx-text-fill: %s", animeStat.equals(Status.FINISHED) ?
                "#ee4b2b" : animeStat.equals(Status.AIRING) ? "#50c878" : "#fccb06");
        this.status.setStyle(style);

        this.episodes.setText(String.format("Episodes: %d", anime.episodes() != null ? anime.episodes() : 0));

        this.rating.setText(String.format("Age rating: %s", anime.rating() == null ? " - " : anime.rating()));

        this.score.setText(String.format("Score: %.2f", anime.score()));

        this.year.setText(String.format("Year : %s", anime.year() != null ? anime.year() : " - "));

        var synopsis = this.anime.synopsis() != null ?
                this.anime.synopsis().replaceAll("\n", "") : "none";

        this.textFlow.getChildren().add(
                new Text(synopsis.length() > 350 ? synopsis.substring(0, 347) + "..." : synopsis));
    }


    private final EventHandler<MouseEvent> cursorEntered = event -> {
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
        this.stage.setScene(scene);
        this.stage.show();
    };
}