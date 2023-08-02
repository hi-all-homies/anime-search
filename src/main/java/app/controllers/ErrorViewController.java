package app.controllers;

import app.exceptions.NotFoundException;
import app.util.DataTransfer;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.ConnectException;

public class ErrorViewController {

    public Label title;
    public ImageView imageView;

    private final DataTransfer dataService;

    public ErrorViewController(DataTransfer dataService) {
        this.dataService = dataService;
    }

    public void initialize(){
        var error = this.dataService.getError();

        Image errImage;

        if (error instanceof NotFoundException)
            errImage = new Image("/assets/404.jpg");
        else if (error instanceof ConnectException)
            errImage = new Image("/assets/no-signal.jpg");
        else
            errImage = new Image("/assets/error.jpeg");
        this.title.setText(error.getMessage());
        this.imageView.setImage(errImage);
    }
}
