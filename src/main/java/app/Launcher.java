package app;

import app.config.ContainerHolder;
import app.controllers.SingleAnimeController;
import app.injector.ViewInjector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Launcher extends Application {

    private ViewInjector viewInjector;

    @Override
    public void init() {
        var container = ContainerHolder.INSTANCE.getContainer();
        container.init();

        container.getLikedStorage().loadLiked();

        this.viewInjector = container.getViewInjector();

        this.viewInjector.addMethod(SingleAnimeController.class,
                () -> new SingleAnimeController(getHostServices()));
    }

    @Override
    public void start(final Stage stage) {
        var root = this.viewInjector.load("/views/main.fxml");

        var bounds = Screen.getPrimary().getBounds();
        double width = bounds.getWidth() * 80 / 100;
        double height = bounds.getHeight() * 85 / 100;

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/assets/icon.png"));
        stage.show();
    }

    @Override
    public void stop(){
        var container = ContainerHolder.INSTANCE.getContainer();
        container.getLikedStorage().storeLiked();
        container.shutdownPools();
    }


    public static void main(String[] args) {
        launch(args);
    }
}