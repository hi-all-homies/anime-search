package app.config;

import app.controllers.*;
import app.model.anime.Anime;
import app.model.request.SearchRequest;
import app.service.anime.AnimeService;
import app.service.anime.DefaultAnimeService;
import app.service.injector.DefaultViewInjector;
import app.service.injector.ViewInjector;
import app.util.DataTransfer;
import app.util.LikedStorage;
import app.util.ViewValueExtractor;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import io.reactivex.rxjava3.subjects.UnicastSubject;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Container {
    private ViewInjector viewInjector;
    private LikedStorage likedStorage;
    private Subject<SearchRequest> reqPublisher;

    public Container() {}

    public Container initServices(){
        this.viewInjector = new DefaultViewInjector();

        Map<Integer, Anime> likedAnime = new HashMap<>(100);
        this.likedStorage = new LikedStorage(likedAnime);

        this.reqPublisher = UnicastSubject.create();

        var builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3500));
        AnimeService animeService = new DefaultAnimeService(builder);

        var dataService = new DataTransfer();

        Subject<String> closeEmitter = BehaviorSubject.create();

        var extractor = new ViewValueExtractor();

        viewInjector.addMethod(AnimeController.class,
                () -> new AnimeController(viewInjector, reqPublisher, dataService, extractor));

        viewInjector.addMethod(AnimeListController.class,
                () -> new AnimeListController(animeService, viewInjector, dataService, reqPublisher, extractor));

        viewInjector.addMethod(SingleAnimeController.class,
                ()->new SingleAnimeController(dataService,animeService,viewInjector,likedAnime,closeEmitter, extractor));

        viewInjector.addMethod(CharacterImageController.class,
                () -> new CharacterImageController(dataService));

        viewInjector.addMethod(ErrorViewController.class,
                () -> new ErrorViewController(dataService));

        viewInjector.addMethod(FavoritesController.class,
                () -> new FavoritesController(likedAnime, reqPublisher, dataService, viewInjector));

        viewInjector.addMethod(MainController.class,
                () -> new MainController(reqPublisher, viewInjector, dataService));

        viewInjector.addMethod(TrailerController.class,
                () -> new TrailerController(dataService, closeEmitter));

        return this;
    }

    public ViewInjector getViewInjector() {
        return viewInjector;
    }

    public LikedStorage getLikedStorage() {
        return likedStorage;
    }

    public Subject<SearchRequest> getReqPublisher() {
        return reqPublisher;
    }
}