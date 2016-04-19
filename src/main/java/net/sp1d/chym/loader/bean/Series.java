/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import net.sp1d.chym.loader.type.LangType;
import net.sp1d.chym.loader.type.IdType;
import net.sp1d.chym.loader.type.TrackerType;

/**
 *
 * @author Sp1D
 */
@Entity
@Table(name = "series")
public class Series implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "series_id")
    private long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn
    private Map<IdType, String> extId;

    @Column(name = "title")
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn
    private Map<LangType, String> foreignTitle;

    @Lob
    @Basic
    @Column(name = "description")
    private String description;

    @Lob
    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn
//    @Column(length = 2000)
    private Map<LangType, String> foreignDescription;

    @Column(name = "start_year")
    private int startYear;

//    @Column(name = "series_end_year")
//    private int endYear;
    @Column(name = "start_date", length = 10)
    private String startDate;

    @Column(name = "end_date", length = 10)
    private String endDate;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @Column(name = "series_country")
//    private List<String> country;
    @Column(name = "poster")
    private String poster;

    @Column(name = "language")
    private String language;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Episode> episodes;

    private ImdbRating imdbRating;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Genre> genres;

    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<TrackerType, TrackerSpecificForSeries> trackerSpecific;

    public void putExtId(IdType idType, String id) {
        if (getExtId() == null) {
            setExtId(new HashMap<>());
        }
        getExtId().put(idType, id);
    }

    public String readExtId(IdType type) {
        return getExtId() != null ? getExtId().get(type) : "";
    }

    public void putForeignTitle(LangType lang, String title) {
        if (getForeignTitle() == null) {
            setForeignTitle(new HashMap<>());
        }
        getForeignTitle().put(lang, title);
    }

    public String readForeignTitle(LangType lang) {
        return getForeignTitle() != null ? getForeignTitle().get(lang) : "";
    }

    public void putForeignDesc(LangType lang, String desc) {
        if (getForeignDescription() == null) {
            setForeignDescription(new HashMap<>());
        }
        getForeignDescription().put(lang, desc);
    }

    public String readForeignDesc(LangType lang) {
        return getForeignDescription() != null ? getForeignDescription().get(lang) : "";
    }

    public void putTrackerSpecific(TrackerType tracker, TrackerSpecificForSeries specific) {
        if (getTrackerSpecific() == null) {
            setTrackerSpecific(new HashMap<>());
        }
        getTrackerSpecific().put(tracker, specific);
    }

    public TrackerSpecificForSeries readTrackerSpecific(TrackerType tracker) {
        if (getTrackerSpecific() == null) {
            return null;
        }
        return getTrackerSpecific().get(tracker);
    }

    public void addEpisode(Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("Episode can not be null");
        }
        if (episodes == null) {
            episodes = new HashSet<>();
        }
        episode.setSeries(this, false);
        episodes.add(episode);
    }

    public void removeEpisode(Episode episode) {
        removeEpisode(episode, true);
    }

    public void removeEpisode(Episode episode, boolean removeFromEpisode) {
        if (episode == null) {
            throw new IllegalArgumentException("Episode can not be null");
        }
        if (episodes == null) {
            episodes = new HashSet<>();
            return;
        }
        if (removeFromEpisode) {
            episode.setSeries(null, false);
        }
        episodes.remove(episode);
    }

    public boolean containsEpisode(Episode episode) {
        if (episodes == null || episode == null) {
            return false;
        }
        for (Episode epi : episodes) {
            if (epi.getSeasonN() == episode.getSeasonN() && epi.getEpisodeN() == episode.getEpisodeN()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return title + " " + startYear;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.extId);
        hash = 53 * hash + Objects.hashCode(this.title);
        hash = 53 * hash + this.startYear;
        return hash;
    }

    /*
    Нельзя забывать, что это скорректированный метод, а не автогенерированный.
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Series other = (Series) obj;
        /*
        Сериалы считаются одинаковыми, если у них совпадет хотя бы одно из наименований
        на трекерах. Работает это всё конечно не быстро, но и вызывается метод не часто, т.к.
        количество уникальных сериалов никогда не будет большим.
        */
        return haveEqualTrackerTitles(this, other);
    }
    
    private List<String> getTrackerTitlesAsList(Series series) {
        
        List<String> seriesTitles = new ArrayList<>();
        
        for (TrackerSpecificForSeries tsfs : series.getTrackerSpecific().values()) {
            if (tsfs.getTitleOnTracker() != null) {
                seriesTitles.add(tsfs.getTitleOnTracker().toLowerCase());
            }
        }
        
        return seriesTitles;
    }
    
    private boolean haveEqualTrackerTitles(Series ser1, Series ser2) {
        List<String> ser1Titles = getTrackerTitlesAsList(ser1);              
        List<String> ser2Titles = getTrackerTitlesAsList(ser2);
        
        for (String s1t : ser1Titles) {
            for (String s2t : ser2Titles) {
                if (s1t.equals(s2t)) {
                    return true;
                }
            }
        }
        
        return false;
    }

//    GENERATED GETTERS AND SETTERS
//    ------------------------------------------------------------------------
//<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<IdType, String> getExtId() {
        return extId;
    }

    public void setExtId(Map<IdType, String> extId) {
        this.extId = extId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<LangType, String> getForeignTitle() {
        return foreignTitle;
    }

    public void setForeignTitle(Map<LangType, String> foreignTitle) {
        this.foreignTitle = foreignTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<LangType, String> getForeignDescription() {
        return foreignDescription;
    }

    public void setForeignDescription(Map<LangType, String> foreignDescription) {
        this.foreignDescription = foreignDescription;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Set<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Set<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ImdbRating getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(ImdbRating imdbRating) {
        this.imdbRating = imdbRating;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Map<TrackerType, TrackerSpecificForSeries> getTrackerSpecific() {
        return trackerSpecific;
    }

    public void setTrackerSpecific(Map<TrackerType, TrackerSpecificForSeries> trackerSpecific) {
        this.trackerSpecific = trackerSpecific;
    }

}
