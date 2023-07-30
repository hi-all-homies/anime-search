package app.config;

import app.injector.ViewInjector;
import app.model.anime.Anime;
import app.service.AnimeService;
import app.util.DataTransferService;
import app.service.DefaultAnimeService;
import app.util.LikedStorage;
import app.util.RequestPublisher;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.*;

public class Container {

    private ConcurrentMap<Integer, Anime> likedAnime;

    private ViewInjector viewInjector;

    private LikedStorage likedStorage;

    private AnimeService animeService;

    private RequestPublisher requestPublisher;

    private DataTransferService dataTransferService;

    private ExecutorService httpPool;

    public static final int LIMIT = 7;


    protected Container (){}

    public void init(){
        this.likedAnime = new ConcurrentHashMap<>(100);
        this.viewInjector = new ViewInjector();
        this.likedStorage = new LikedStorage();
        this.requestPublisher = new RequestPublisher();

        this.httpPool = Executors.newFixedThreadPool(3);

        var builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3500))
                .executor(this.httpPool);

        this.animeService = new DefaultAnimeService();

        this.dataTransferService = new DataTransferService();
    }

    public RequestPublisher getRequestPublisher() {
        return requestPublisher;
    }

    public ConcurrentMap<Integer, Anime> getLikedAnime() {
        return likedAnime;
    }

    public ViewInjector getViewInjector() {
        return viewInjector;
    }

    public LikedStorage getLikedStorage() {
        return likedStorage;
    }

    public AnimeService getAnimeService() {
        return animeService;
    }

    public DataTransferService getDataTransferService() {
        return dataTransferService;
    }

    public void shutdownPools(){
        this.httpPool.shutdownNow();
    }
}