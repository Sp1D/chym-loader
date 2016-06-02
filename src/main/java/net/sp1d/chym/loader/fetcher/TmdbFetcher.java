/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.fetcher;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.model.tv.TVBasic;
import com.omertron.themoviedbapi.model.tv.TVInfo;
import com.omertron.themoviedbapi.results.ResultList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sp1d.chym.loader.bean.Genre;
import net.sp1d.chym.loader.bean.ImdbRating;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.prototype.Fetcher;
import net.sp1d.chym.loader.repo.GenreRepo;
import net.sp1d.chym.loader.type.IdType;
import net.sp1d.chym.loader.type.LangType;
import org.apache.http.MethodNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sp1D
 */
@Component
public class TmdbFetcher implements Fetcher {

    private static final Logger LOG = LogManager.getLogger(TmdbFetcher.class);
    private Map<Integer, Genre> genreCache = new HashMap<>();

    @Autowired
    TheMovieDbApi tmdb;

    @Autowired
    ImdbFetcher imdbFetcher;
    
    @Autowired
    GenreRepo genreRepo;

    @Override
    public void fetchSeries(List<Series> series) throws MovieDbException, IOException, MethodNotSupportedException {
        int i = 0;
        for (Series ser : series) {
            i++;

            int tmdbId;

            if (ser.readExtId(IdType.TMDB) == null) {
                ResultList<TVBasic> tvblist = null;
                
//                Пытаемся достучаться до TMDB определенное количество раз
                int repeats = 3;
                for (int j = 0; j < repeats; j++) {
                    try {
                        tvblist = tmdb.searchTV(ser.getTitle(), 0, null, ser.getStartYear() > 0 ? ser.getStartYear() : 0, SearchType.PHRASE);
                        break;
                    } catch (MovieDbException ex) {
                        if (j == repeats-1) {
                            throw ex;
                        }
                    }
                }
                if (LOG.isDebugEnabled()) {
                    if (tvblist.getResults().isEmpty()) {
                        LOG.debug("{} {} not found on TMDB!", ser.getTitle(), ser.getStartYear());
                    } else
                    for (TVBasic result : tvblist.getResults()) {
                        LOG.debug("Searching on TMDB for {} {}, found {} {}", ser.getTitle(), ser.getStartYear(), result.getName(), result.getFirstAirDate());
                    }
                }
                if (tvblist.getResults().size() > 0) {
                    tmdbId = tvblist.getResults().get(0).getId();
                } else {
                    continue;
                }
            } else {
                tmdbId = Integer.valueOf(ser.readExtId(IdType.TMDB));
            }
//            Получаем данные на англ языке
            TVInfo info = tmdb.getTVInfo(tmdbId, LangType.EN.toString().toLowerCase());

            ser.setTitle(info.getName());
            ser.setDescription(info.getOverview());
            ser.setPoster(info.getPosterPath());
            ser.putExtId(IdType.TMDB, String.valueOf(info.getId()));
            ser.putExtId(IdType.IMDB, tmdb.getTVExternalIDs(tmdbId, LangType.EN.toString().toLowerCase()).getImdbId());
            ser.setStartDate(info.getFirstAirDate().trim());
            ser.setEndDate(info.getLastAirDate().trim());
            ser.setLanguage(info.getOriginalLanguage());
            List<com.omertron.themoviedbapi.model.Genre> enGenres = info.getGenres();

//            Получаем данные на русском языке
            info = tmdb.getTVInfo(tmdbId, LangType.RU.toString().toLowerCase());

            ser.putForeignTitle(LangType.RU, info.getName());
            ser.putForeignDesc(LangType.RU, info.getOverview());
            List<com.omertron.themoviedbapi.model.Genre> foreignGenres = info.getGenres();

            ser.setGenres(combineTmdbGenres(enGenres, foreignGenres, LangType.RU));

            LOG.debug("Got info from TMDB for {}", ser.getTitle());

            ser.setImdbRating(imdbFetcher.getRating(ser.readExtId(IdType.IMDB)));
            LOG.debug("Got rating from IMDB for {} : {}({})", ser.getTitle(), ser.getImdbRating().getRating(), ser.getImdbRating().getVotes());

//            if (i == 3) {
//                break;
//            }
        }
    }

    private Set<Genre> combineTmdbGenres(
            List<com.omertron.themoviedbapi.model.Genre> enGenres,
            List<com.omertron.themoviedbapi.model.Genre> foreignGenres, LangType lang) {
        Set<Genre> output = new HashSet<>();
        for (com.omertron.themoviedbapi.model.Genre enGenre : enGenres) {
            Genre genre = genreRepo.findOne(enGenre.getId());
            if (genre != null) {
                output.add(genre);
                continue;
            } else {
                genre = genreCache.get(enGenre.getId());
                if (genre != null) {
                    output.add(genre);
                    continue;
                }
            }
            
            Genre newGenre = new Genre();
            newGenre.setId(enGenre.getId());
            newGenre.setName(enGenre.getName());
            String foreignName = null;
            for (com.omertron.themoviedbapi.model.Genre foGenre : foreignGenres) {
                if (foGenre.getId() == enGenre.getId()) {
                    foreignName = foGenre.getName();
                    break;
                }
            }
            if (foreignName != null) newGenre.putForeignName(lang, foreignName);
            output.add(newGenre);
            genreCache.put(newGenre.getId(), newGenre);
        }
        return output;
    }
/*
    private List<Genre> convertGenres(List<com.omertron.themoviedbapi.model.Genre> tmdbGenres) {
        List<Genre> ourGenres = new ArrayList<>();
        for (com.omertron.themoviedbapi.model.Genre tmdbGenre : tmdbGenres) {
            Genre genre = new Genre();
            genre.setName(tmdbGenre.getName());
            genre.setId(tmdbGenre.getId());
            ourGenres.add(genre);
        }
        return ourGenres;
    }
*/
}
