/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.notifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sp1D
 */
@Component
public class Updates {
//    private Set<Episode> episodesSet = new HashSet<>();
    private List<Series> series = new ArrayList<>();
    private Map<Series, Set<Episode>> episodes = new HashMap<>();
    
    public void addEpisodes(Series series, Set<Episode> episodes) {
        Set<Episode> setEpisodes = getEpisodes().get(series);
        if (setEpisodes == null) {
            setEpisodes = new HashSet<>();
        }
        setEpisodes.addAll(episodes);
        getEpisodes().put(series, setEpisodes);
    }
//    
//    public boolean removeEpisode(Episode episode) {
//        return getEpisodes().remove(episode);
//    }
    
    public void addSeries(Series series) {
        getSeries().add(series);
    }
    
    public boolean removeSeries(Series series) {
        return getSeries().remove(series);
    }
    
    public void reset(){
        setSeries(new ArrayList<>());
        setEpisodes(new HashMap<>());
    }
    
//    GENERATED GETTERS AND SETTERS
//    ------------------------------------------------------------------------

//    public Set<Episode> getEpisodesSet() {
//        return episodesSet;
//    }
//
//    public void setEpisodesSet(Set<Episode> episodesSet) {
//        this.episodesSet = episodesSet;
//    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }

    public Map<Series, Set<Episode>> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Map<Series, Set<Episode>> episodes) {
        this.episodes = episodes;
    }

    
    
    
}
