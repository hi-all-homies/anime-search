package app.controllers;

import app.util.DataTransfer;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TrailerController {
    public WebView webView;
    private WebEngine webEngine;

    private final DataTransfer dataService;
    private final Subject<String> closeEmitter;

    public TrailerController(DataTransfer dataService, Subject<String> closeEmitter) {
        this.dataService = dataService;
        this.closeEmitter = closeEmitter;
    }

    public void initialize(){
        this.webEngine = this.webView.getEngine();
        var anime = this.dataService.getAnime();
        this.webEngine.load(anime.trailer().embedUrl());

        closeEmitter.subscribe(signal -> {
            if (signal.equals(anime.title())){
                webEngine.getLoadWorker().cancel();
                webEngine.load(null);
            }
        });
    }
}