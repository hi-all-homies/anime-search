package app.controllers;

import app.Launcher;
import app.model.anime.Anime;
import app.util.DataTransfer;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Screen;

public class DialogController {
    public DialogPane dialog;
    public Label dialogTitle;
    public ListView<Anime> list;

    private final DataTransfer dataService;

    public DialogController(DataTransfer dataService) {
        this.dataService = dataService;
    }


    public void initialize(){
        this.dialog.getStylesheets().add(
                Launcher.DARK_MODE ? "/main.css" : "/light.css");

        var bounds = Screen.getPrimary().getBounds();
        this.dialog.setPrefSize(bounds.getWidth()*60/100, bounds.getHeight()*40/100);

        var data = this.dataService.getDialogData();
        this.dialogTitle.setText(String.format(
                "Anime of %s genre by %d year you've liked: ", data.genre(), data.year()));

        this.list.setCellFactory(listView -> new AnimeCell());
        this.list.setItems(FXCollections.observableArrayList(data.likedByYear()));
    }


    private static class AnimeCell extends ListCell<Anime> {

        @Override
        protected void updateItem(Anime item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null)
                setGraphic(null);
            else {
                var title = new Text(item.title());
                var episodes = new Label(item.episodes() + " episodes");
                var source = new Label("source: " + item.source());
                var filler = new Pane();
                var separator = new Separator(Orientation.VERTICAL);
                HBox.setHgrow(filler, Priority.SOMETIMES);

                var content = new HBox(title, filler, episodes, separator, source);
                content.setSpacing(10);
                setGraphic(content);
            }
        }
    }
}