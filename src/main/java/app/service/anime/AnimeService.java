package app.service.anime;

import app.model.anime.Anime;
import app.model.anime.enums.AgeRating;
import app.model.anime.enums.Status;
import app.model.anime.enums.Type;
import app.model.personage.Personage;
import io.reactivex.rxjava3.core.Observable;


public interface AnimeService {

    Observable<Anime> findById(int id);

    Observable<Anime> findByQuery(String query, int page, int limit, Type type,AgeRating ageRating,
                                  Status status, String minScore, int ...genres);

    Observable<Anime> findTop(int page, int limit, Type type, AgeRating ageRating, Status filter);

    Observable<Anime> findOngoings(int page, int limit, Type filter);

    Observable<Personage> findPersonageById(int animeId);
}