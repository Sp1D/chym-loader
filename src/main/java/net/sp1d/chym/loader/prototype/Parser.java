/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.prototype;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.Torrent;

/**
 *
 * @author Sp1D
 */
public interface Parser {
    List<Series> loadSeries() throws IOException;
    Set<Episode> loadNewEpisodesBySeries(Series series) throws IOException;
    List<Torrent> loadTorrentsByEpisode(Episode episode) throws IOException;
    List<Series> getUpdatedSeriesByRss(List<Series> allSeries) throws IOException;
    void exit();
}
