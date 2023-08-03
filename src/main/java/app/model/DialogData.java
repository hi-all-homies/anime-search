package app.model;

import app.model.anime.Anime;
import java.util.Collection;

public record DialogData(String genre, int year, Collection<Anime> likedByYear) {}
