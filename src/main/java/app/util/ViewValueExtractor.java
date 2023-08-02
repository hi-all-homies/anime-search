package app.util;

import app.model.anime.Anime;
import app.model.anime.enums.Status;
import java.util.regex.Pattern;

public class ViewValueExtractor {

    public String getTitle(Anime anime){
        return anime.titleEnglish() != null ?
                anime.titleEnglish() : anime.title();
    }

    public int getYear(Anime anime){
        int year = anime.year() != null ? anime.year() :
                anime.aired().prop().from().year() != null ?
                        anime.aired().prop().from().year() : -1;

        if (year == -1 && anime.aired().range() != null) {

            Pattern pattern = Pattern.compile("(\\d{4})");
            var matcher = pattern.matcher(anime.aired().range());

            if (matcher.find())
                year = Integer.parseInt(matcher.group());
        }
        return year;
    }

    public String getYearString(Anime anime){
        int year = this.getYear(anime);
        return String.format("Year : %s", year != -1 ? year : " - ");
    }

    public String getStatusStyle(Anime anime){
        return String.format("-fx-text-fill: %s", anime.status().equals(Status.COMPLETE) ?
                "#ee4b2b" : anime.status().equals(Status.AIRING) ? "#50c878" : "#fccb06");
    }

    public String getSynopsis(Anime anime){
        return anime.synopsis() != null ? anime.synopsis().replaceAll("\n", "") : "none";
    }

    public String getEpisodes(Anime anime){
        return String.format("Episodes: %d", anime.episodes() != null ? anime.episodes() : 0);
    }

    public String getRating(Anime anime){
        return String.format("Age rating: %s", anime.rating() == null ? " - " : anime.rating().description);
    }
}
