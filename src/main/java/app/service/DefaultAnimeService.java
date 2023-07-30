package app.service;

import app.model.anime.Anime;
import app.model.personage.Personage;
import app.model.response.Response;
import app.model.response.ResponseList;
import app.model.response.ResponseSingle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class DefaultAnimeService implements AnimeService{

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public static final String BASE_URL = "https://api.jikan.moe/v4/";

    public DefaultAnimeService() {
        this.httpClient = HttpClient.newBuilder().build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public Observable<Anime> findById(int id) {
        var path = new StringBuilder(BASE_URL)
                .append("anime/").append(id);

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.singleAnime))
                .map(Response::getSingleData);
    }

    @Override
    public Observable<Anime> findByQuery(String query, int page, int limit, int... genres) {
        var path = new StringBuilder(BASE_URL)
                .append("anime?page=").append(page)
                .append("&limit=").append(limit);

        if (!"".equals(query))
            path.append("&q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8));
        if (genres.length > 0){
            path.append("&genres=");
            for (int i = 0; i < genres.length; i++){
                path.append(genres[i]);
                if (i < genres.length - 1)
                    path.append(",");
            }
        }

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedAnime))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }

    @Override
    public Observable<Anime> findTop(int page, int limit) {
        var path = new StringBuilder(BASE_URL)
                .append("top/anime?page=").append(page)
                .append("&limit=").append(limit);

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedAnime))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }

    @Override
    public Observable<Anime> findOngoings(int page, int limit) {
        var path = new StringBuilder(BASE_URL)
                .append("seasons/now?page=").append(page)
                .append("&limit=").append(limit);

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedAnime))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }

    @Override
    public Observable<Personage> findPersonageById(int animeId) {
        var path = new StringBuilder(BASE_URL)
                .append("anime/").append(animeId)
                .append("/").append("characters");

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedCharacter))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }


    private HttpRequest getRequest(StringBuilder path){
        return HttpRequest.newBuilder(URI.create(path.toString()))
                .GET().build();
    }

    private Observable<InputStream> sendRequest(final HttpRequest req) {
        return Observable
                .fromCallable(() ->
                        this.httpClient.send(
                                req, HttpResponse.BodyHandlers.ofInputStream()).body())
                .subscribeOn(Schedulers.io());
    }

    private final TypeReference<ResponseSingle<Anime>> singleAnime = new TypeReference<>(){};
    private final TypeReference<ResponseList<Anime>> listedAnime = new TypeReference<>(){};
    private final TypeReference<ResponseList<Personage>> listedCharacter = new TypeReference<>(){};

    private <T> Response<T> readInputStream (
            InputStream in, TypeReference<? extends Response<T>> ref) throws IOException {

        return this.mapper.readValue(in, ref);
    }
}