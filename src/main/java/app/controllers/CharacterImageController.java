package app.controllers;

import app.util.DataTransferService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CharacterImageController {
    public Label name;
    public Label role;
    public ImageView image;

    private ContextMenu menu;

    private final DataTransferService dataService;

    public CharacterImageController(DataTransferService dataService) {
        this.dataService = dataService;
    }

    public void initialize(){
        var personage = this.dataService.getPersonage();

        this.name.setText(personage.character().name());
        this.role.setText(personage.role().name());
        this.image.setImage(new Image(personage.character().images().jpg().imageUrl()));

        this.menu = new ContextMenu();
        this.menu.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                event.consume();
        });

        var save = new MenuItem("save picture");
        save.setOnAction(event -> {
            this.saveImage();
            this.menu.hide();
        });
        this.menu.getItems().add(save);

        this.image.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY){
                this.menu.hide();
                this.menu.show(this.image, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void saveImage() {
        try {
            var dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Where to save");
            dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            var dir = dirChooser.showDialog(this.name.getScene().getWindow());
            if (dir != null) {
                var picFile = new File(dir, this.name.getText() + ".jpg");
                InputStream in = new URL(this.image.getImage().getUrl()).openStream();
                Files.copy(in, picFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                this.getNotifier("image successfully saved", "#50c878")
                        .show(this.name.getScene().getWindow());
            }
        } catch (IOException e){
            this.getNotifier("failed to save image", "crimson")
                    .show(this.name.getScene().getWindow());
        }
    }

    private Popup getNotifier(String message, String color){
        final Popup notifier = new Popup();
        notifier.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        notifier.setAutoHide(true);
        notifier.setAutoFix(true)
        ;
        var messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Comic Sans MS'");

        var hBox = new HBox(messageLabel);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(5d));
        hBox.setStyle(String.format("-fx-background-color: %s", color));

        notifier.getContent().add(hBox);
        return notifier;
    }
}