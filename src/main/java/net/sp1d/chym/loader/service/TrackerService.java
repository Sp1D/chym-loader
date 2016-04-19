/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.service;

import com.omertron.themoviedbapi.MovieDbException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.Torrent;
import net.sp1d.chym.loader.prototype.Fetcher;
import net.sp1d.chym.loader.prototype.Tracker;
import org.apache.http.MethodNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sp1D
 */
@Component
@Service
public class TrackerService {

    private static final Logger LOG = LogManager.getLogger(TrackerService.class);

    

    @Autowired
    List<Tracker> trackers;

    public List<Series> loadNewSeriesFromTracker(Tracker tracker, List<Series> existingSeries) throws IOException {
        LOG.info("Loading new series from tracker {}", tracker.getClass().getSimpleName());
        
        List<Series> newSeries = tracker.loadSeries();
        newSeries.removeAll(existingSeries);

        LOG.info("{} new series was found", newSeries.size());
        return newSeries;
    }

    public List<Series> loadNewSeries(List<Series> existingSeries) throws IOException {
        LOG.info("Loading new series from all trackers");
        
        List<Series> newSeries = new ArrayList<>();
        for (Tracker tracker : trackers) {
            List<Series> trackerSeries = tracker.loadSeries();
            trackerSeries.removeAll(existingSeries);
            newSeries.addAll(trackerSeries);
        }

        LOG.info("{} new series was found", newSeries.size());
        return newSeries;
    }

    /**
     * Загружает все эпизоды сериала с трекера, удаляет из полученного списка те
     * эпизоды, которые уже есть в объекте series, полученный в итоге список
     * новых эпизодов прибавляет к сериалу и этот же список новых эпизодов
     * возвращает.
     *
     * @param tracker
     * @param series
     * @return
     * @throws IOException
     */
    public Set<Episode> loadNewEpisodesBySeries(Tracker tracker, Series series) throws IOException {
        if (tracker == null || series == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        LOG.info("Loading new episodes of series {}", series.getTitle());
        if (series.getEpisodes() == null) {
            series.setEpisodes(new HashSet<>());
        }
        Set<Episode> newEpisodes = tracker.loadNewEpisodesBySeries(series);

        LOG.info("{} new episodes was found", newEpisodes.size());
        return newEpisodes;
    }

    public Set<Episode> loadNewEpisodesBySeries(Series series) throws IOException {
        if (trackers == null || series == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        LOG.info("Loading new episodes of series {}", series.getTitle());
        Set<Episode> allNewEpisodes = new HashSet<>();

        for (Tracker tracker : trackers) {
            Set<Episode> trackerNewEpisodes = tracker.loadNewEpisodesBySeries(series);
            
            allNewEpisodes.addAll(trackerNewEpisodes);
        }

        LOG.info("{} new episodes was found", allNewEpisodes.size());
        return allNewEpisodes;
    }

    

    public List<Torrent> loadTorrentsByEpisode(Episode episode) throws IOException {
        LOG.info("Loading new torrents for episode S{}E{} \"{}\"", 
                episode.getSeasonN(), episode.getEpisodeN(), episode.getSeries().getTitle());
        
        List<Torrent> torrents = new ArrayList<>();
        for (Tracker tracker : trackers) {
            torrents.addAll(tracker.loadTorrentsByEpisode(episode));
        }
        return torrents;
    }

    public List<Torrent> loadTorrentsByRss(List<Series> allSeries) throws IOException {
        LOG.info("Loading new torrents via RSS feed");
        
        List<Torrent> newTorrents = new ArrayList<>();
        for (Tracker tracker : trackers) {

            List<Series> updatedSeries = tracker.getUpdatedSeriesByRss(allSeries);            

            for (Series ser : updatedSeries) {
                Set<Episode> newEpisodes = loadNewEpisodesBySeries(ser);
                for (Episode newEpisode : newEpisodes) {
                    newTorrents.addAll(loadTorrentsByEpisode(newEpisode));
                }
            }
        }
        return newTorrents;
    }
    
    public List<Series> loadUpdatedSeriesByRss(List<Series> existingSeries) throws IOException {
        LOG.info("Loading updated series via RSS feed");        
        
        List<Series> updSeries = new ArrayList<>();        
        for (Tracker tracker : trackers) {
            updSeries.addAll(tracker.getUpdatedSeriesByRss(existingSeries));
        }
        return updSeries;
    }

}
