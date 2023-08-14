package app;

import app.config.Container;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Launcher extends Application {
    private Container container;

    public static volatile Scene scene;
    public static boolean DARK_MODE = true;

    public static void setTheme(){
        if (DARK_MODE){
            scene.getStylesheets().clear();
            scene.setUserAgentStylesheet(null);
            scene.getStylesheets().add("/main.css");
        }
        else {
            scene.getStylesheets().clear();
            scene.setUserAgentStylesheet(null);
            scene.getStylesheets().add("/light.css");
        }
    }

    public static void changeTheme(){
        DARK_MODE = ! DARK_MODE;
    }

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

        scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/assets/icon.png"));
        stage.setTitle("Anime searcher");
        setTheme();
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