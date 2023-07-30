package app.service;

import app.model.anime.Anime;
import app.model.personage.Personage;
import io.reactivex.rxjava3.core.Observable;


public interface AnimeService {

    Observable<Anime> findById(int id);

    Observable<Anime> findByQuery(String query, int page, int limit, int ...genres);

    Observable<Anime> findTop(int page, int limit);

    Observable<Anime> findOngoings(int page, int limit);

    Observable<Personage> findPersonageById(int animeId);

}
