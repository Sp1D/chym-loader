/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.tracker;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.Torrent;
import net.sp1d.chym.loader.prototype.AbstractTracker;
import net.sp1d.chym.loader.prototype.Parser;
import net.sp1d.chym.loader.prototype.Tracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sp1D
 */
@Component
public class LfTracker extends AbstractTracker implements Tracker {

    private static final Logger LOG = LogManager.getLogger(LfTracker.class);

    @Autowired
    Parser lostfilmParser;

    public LfTracker() {
        name = "Lostfilm";
        website = "http://www.lostfilm.tv";
    }

    @Override
    public List<Series> loadSeries() throws IOException {
        LOG.info("{} loading all series", this.name);
        return lostfilmParser.loadSeries();
    }

    @Override
    public Set<Episode> loadNewEpisodesBySeries(Series series) throws IOException {
        LOG.info("{} loading all episodes of series \"{}\"", this.name, series.getTitle());
        return lostfilmParser.loadNewEpisodesBySeries(series);
    }

    @Override
    public List<Torrent> loadTorrentsByEpisode(Episode episode) throws IOException {        
        LOG.info("{} loading all torrents of episode S{} E{}", this.name, episode.getSeasonN(), episode.getEpisodeN());
        return lostfilmParser.loadTorrentsByEpisode(episode);
    }


    @Override
    public void exit() {
        LOG.info("{} exiting", this.name);
        lostfilmParser.exit();
    }

    @Override
    public List<Series> getUpdatedSeriesByRss(List<Series> allSeries) throws IOException {
        LOG.info("{} loading updated series by RSS feed", this.name);        
        return lostfilmParser.getUpdatedSeriesByRss(allSeries);
    }
    
    

}
