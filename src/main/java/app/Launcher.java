package app;

import app.config.Container;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Launcher extends Application {
    private Container container;

    @Override
    public void init() {
        this.container = new Container()
                .initServices();

        this.container.getLikedStorage().loadLiked();
    }

    @Override
    public void start(final Stage stage) {
        var root = this.container.getViewInjector()
                .load("/views/main.fxml");

        var bounds = Screen.getPrimary().getBounds();
        double width = bounds.getWidth() * 80 / 100;
        double height = bounds.getHeight() * 85 / 100;

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/assets/icon.png"));
        stage.setTitle("Anime searcher");
        stage.show();
    }

    @Override
    public void stop(){
        this.container.getLikedStorage().storeLiked();
        this.container.getReqPublisher().onComplete();
    }


    public static void main(String[] args) {
        launch(args);
    }
}