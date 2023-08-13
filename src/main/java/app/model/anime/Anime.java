package app.model.anime;

import app.model.anime.aired.Aired;
import app.model.anime.enums.AgeRating;
import app.model.anime.enums.Status;
import app.model.anime.enums.Type;
import app.model.anime.song.Songs;
import app.model.relations.Relation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import app.model.img.Images;
import java.io.Serializable;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Anime(
        @JsonProperty("mal_id") int id, Images images, Trailer trailer,
        String title, @JsonProperty("title_english") String titleEnglish,
        Type type, String source, Integer episodes, Status status,
        Aired aired, String duration, AgeRating rating, double score,
        String synopsis, String background, Integer year, Collection<Studio> studios,
        Collection<GenreEntity> genres, Collection<GenreEntity> themes,
        Songs theme, Collection<Relation> relations) implements Serializable {


    public Anime(Anime original, String title, int year){

        this(original.id, original.images, original.trailer, title, original.titleEnglish,
                original.type, original.source, original.episodes, original.status,
                original.aired, original.duration, original.rating, original.score,
                original.synopsis, original.background, year, original.studios,
                original.genres, original.themes, original.theme, original.relations);
    }
}