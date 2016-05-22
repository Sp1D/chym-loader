/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader;

import com.omertron.themoviedbapi.MovieDbException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.notifier.Notifier;
import net.sp1d.chym.loader.notifier.Updates;
import net.sp1d.chym.loader.prototype.Tracker;
import net.sp1d.chym.loader.repo.SeriesRepo;
import net.sp1d.chym.loader.service.FetcherService;
import net.sp1d.chym.loader.service.TrackerService;
import net.sp1d.chym.loader.service.SeriesService;
import net.sp1d.chym.loader.service.UserService;
import org.apache.http.MethodNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Sp1D
 */
@Component
public class Loader {

    @Autowired
    SeriesService seriesService;

    @Autowired
    TrackerService commonService;
    
    @Autowired
    FetcherService fetcherService;

    @Autowired
    UserService userService;

    @Autowired
    SeriesRepo seriesRepo;

    @Autowired
    List<Tracker> trackers;

    @Autowired
    Updates updates;

    @Autowired
    Notifier notifier;

    private List<Series> series;

    private static final Logger LOG = LogManager.getLogger(Loader.class);

    @Transactional
    public void init() {
        System.out.println("Initializing loader");

    }

    /**
     * Загрузка новых сериалов и новых эпизодов в них. Используется отдельно или
     * в связке с fetch() при первом запуске приложения, т.к. fetch будет
     * загружать описания для всех сериалов подряд.
     *
     * При первом запуске можно также использовать и update().
     */
    @Transactional
    public void load() {        
        LOG.info("Loading torrents");
        
        series = seriesRepo.findAll();
        if (series == null) {
            series = new ArrayList<>();
        }

        List<Series> newSeries;

        try {
            /*
            Поиск новых сериалов на всех трекерах
             */
            newSeries = commonService.loadNewSeries(series);
            series.addAll(newSeries);

            /*
            Обход ВСЕХ, не только новых сериалов на всех трекерах в поисках
            новых эпизодов
             */
            for (Series sery : series) {

                Set<Episode> seryNewEpisodes = commonService.loadNewEpisodesBySeries(sery);

//                updates.addEpisodes(sery, seryNewEpisodes);
                int i = 0;
                /*
                Обход всех НОВЫХ эпизодов сериала для поиска торрентов
                 */
                for (Episode newEpisode : seryNewEpisodes) {
                    i++;
                    commonService.loadTorrentsByEpisode(newEpisode);
                    /*
                    В тестовых целях ограничение количества скачиваемых
                    от каждого сериала торрентов
                     */
                    if (i == 1) {
                        break;
                    }
                }

            }
            LOG.info("Saving all loaded data");
            seriesRepo.save(series);
        } catch (Exception ex) {
            processException(ex);
        }

    }

    /**
     * Загрузка описаний ВСЕХ сериалов. Должна использоваться для перезагрузки
     * описаний, например, вследствие ошибок в описаниях или в БД.
     */
    @Transactional
    public void fetch() {
        LOG.info("Fetching descriptions for all series");
        series = seriesRepo.findAll();
        try {
            fetcherService.fetchSeries(series);
        } catch (MovieDbException | MethodNotSupportedException | IOException ex) {
            processException(ex);
        }
        LOG.info("Saving all loaded data");
        seriesRepo.save(series);
    }

    //    @Transactional
    public void tempTest() {

    }

    @Transactional
    public void reset() {
        tempTest();
//        System.out.println("Resetting database!");
//        try {
//            
////        series = seriesRepo.findAll();
////        seriesRepo.delete(series);
////        seriesRepo.save(series);
//        } catch (IOException ex) {
//            processException(ex);
//        }
    }

    /**
     * Загрузка новых сериалов и эпизодов в них, а затем загрузка описаний для
     * новинок. Отличается от load() тем, что load() не загружает описания.
     * Отличается от цепочки load() -> fetch(), тем, что при вызове fetch()
     * будут загружены ВСЕ описания, а не только для новинок.
     *
     */
    @Transactional
    public void update() {
        LOG.info("Updating all series");
        updates.reset();
        series = seriesRepo.findAll();
        if (series == null) {
            series = new ArrayList<>();
        }

        List<Series> newSeries = new ArrayList<>();

        try {
            newSeries = commonService.loadNewSeries(series);
            series.addAll(newSeries);

            for (Series sery : series) {

                Set<Episode> seryNewEpisodes = commonService.loadNewEpisodesBySeries(sery);
                
                if (!seryNewEpisodes.isEmpty()) updates.addEpisodes(sery, seryNewEpisodes);

                int i = 0;
                for (Episode episode : seryNewEpisodes) {
                    i++;
                    commonService.loadTorrentsByEpisode(episode);
//                    if (i == 1) {
//                        break;
//                    }
                }

            }
            fetcherService.fetchSeries(newSeries);
            
            int totalNewEpisodes = 0;
            for (Set<Episode> episodes : updates.getEpisodes().values()) {
                totalNewEpisodes += episodes.size();
            }
            LOG.info(updates.getSeries().size() + " new series, " + 
                     totalNewEpisodes + " new episodes was found");
            
        } catch (Exception ex) {
            processException(ex);
        }

        LOG.info("Saving all loaded data");
        seriesRepo.save(series);

        LOG.info("Notifying users about updates");
        updates.getSeries().addAll(newSeries);
        try {
            notifier.notifyUsers();
        } catch (IOException ex) {
            processException(ex);
        }

    }

    @Transactional
    public void rssUpdate() {
        LOG.info("Reading rss feeds for updates");
        updates.reset();
        series = seriesRepo.findAll();
        try {            
            List<Series> updatedSeries = commonService.loadUpdatedSeriesByRss(series);

            for (Series ser : updatedSeries) {
                Set<Episode> newEpisodes = commonService.loadNewEpisodesBySeries(ser);
                updates.addEpisodes(ser, newEpisodes);
                
                for (Episode newEpisode : newEpisodes) {
                    commonService.loadTorrentsByEpisode(newEpisode);
                }
            }
            
            int totalNewEpisodes = 0;
            for (Set<Episode> episodes : updates.getEpisodes().values()) {
                totalNewEpisodes += episodes.size();
            }
            LOG.info(updates.getSeries().size() + " new series, " + 
                     totalNewEpisodes + " new episodes was found");

        } catch (IOException ex) {
            processException(ex);
        }
        LOG.info("Saving all loaded data");
        seriesRepo.save(series);
        
        LOG.info("Notifying users about updates");
        try {
            notifier.notifyUsers();
        } catch (IOException ex) {
            processException(ex);
        }
    }

    private void processException(Exception ex) {
        LOG.fatal("Fatal error", ex);

    }

    public void exit() {
        for (Tracker tracker : trackers) {
            tracker.exit();
        }
    }
}
