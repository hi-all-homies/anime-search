package app.controllers;

import app.config.ContainerHolder;
import app.exceptions.NotFoundException;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.ConnectException;

public class ErrorViewController {

    public Label title;
    public ImageView imageView;


    public void initialize(){
        var error = ContainerHolder.INSTANCE.getContainer()
                .getDataTransferService().getError().getCause();

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
