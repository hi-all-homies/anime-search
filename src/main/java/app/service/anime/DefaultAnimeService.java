package app.service.anime;

import app.model.anime.Anime;
import app.model.anime.enums.AgeRating;
import app.model.anime.enums.Status;
import app.model.anime.enums.Type;
import app.model.anime.song.Songs;
import app.model.personage.Personage;
import app.model.relations.Relation;
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

    public DefaultAnimeService(HttpClient.Builder builder) {
        this.httpClient = builder.build();
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
    public Observable<Anime> findByQuery(String query, int page, int limit, Type type,
                                         AgeRating ageRating, Status status, String minScore,
                                         int... genres) {

        var path = this.buildSearchQuery(
                query, page, limit, type, ageRating, status, minScore, genres);

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedAnime))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }

    private StringBuilder buildSearchQuery(String query, int page, int limit, Type type, AgeRating ageRating, Status status, String minScore, int[] genres) {
        var path = new StringBuilder(BASE_URL)
                .append("anime?page=").append(page)
                .append("&limit=").append(limit);

        if (!type.equals(Type.ALL))
            path.append("&type=").append(type.name);
        if (!ageRating.equals(AgeRating.All))
            path.append("&rating=").append(ageRating.name);
        if (!status.equals(Status.All))
            path.append("&status=").append(status.name);
        if (!minScore.equals("all"))
            path.append("&min_score=").append(minScore);

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
        return path;
    }

    @Override
    public Observable<Anime> findTop(int page, int limit, Type type, AgeRating ageRating,
                                     Status filter) {
        
        var path = this.buildTopQuery(page, limit, type, ageRating, filter);

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedAnime))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }

    private StringBuilder buildTopQuery(int page, int limit, Type type, AgeRating ageRating, Status filter) {
        var path = new StringBuilder(BASE_URL)
                .append("top/anime?page=").append(page)
                .append("&limit=").append(limit);

        if (!type.equals(Type.ALL))
            path.append("&type=").append(type.name);
        if (!ageRating.equals(AgeRating.All))
            path.append("&rating=").append(ageRating.name);
        if (!filter.equals(Status.All))
            path.append("&filter=").append(filter.name);
        return path;
    }

    @Override
    public Observable<Anime> findOngoings(int page, int limit, Type filter) {
        var path = this.buildOngoingsQuery(page, limit, filter);

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.listedAnime))
                .flatMap(resp -> Observable.fromIterable(resp.getListedData()));
    }

    private StringBuilder buildOngoingsQuery(int page, int limit, Type filter) {
        var path = new StringBuilder(BASE_URL)
                .append("seasons/now?page=").append(page)
                .append("&limit=").append(limit);

        if (!filter.equals(Type.ALL))
            path.append("&filter=").append(filter.name);
        return path;
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

    @Override
    public Observable<Songs> findSongs(int animeId){
        var path = new StringBuilder(BASE_URL)
                .append("anime/").append(animeId)
                .append("/").append("themes");

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.songs))
                .map(Response::getSingleData);
    }

    @Override
    public Observable<Relation> findRelations(int animeId){
        var path = new StringBuilder(BASE_URL)
                .append("anime/").append(animeId)
                .append("/").append("relations");

        return this.sendRequest(this.getRequest(path))
                .map(in -> this.readInputStream(in, this.relationList))
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
    private final TypeReference<ResponseSingle<Songs>> songs = new TypeReference<>() {};
    private final TypeReference<ResponseList<Relation>> relationList = new TypeReference<>() {};

    private <T> Response<T> readInputStream (
            InputStream in, TypeReference<? extends Response<T>> ref) throws IOException {

        return this.mapper.readValue(in, ref);
    }
}