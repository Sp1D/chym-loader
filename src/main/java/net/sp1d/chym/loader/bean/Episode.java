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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import net.sp1d.chym.loader.type.LangType;

/**
 *
 * @author Sp1D
 */
@Entity
@Table(name = "episode")
public class Episode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "episode_id")
    private long id;
    
    @Column(name = "season")
    private int seasonN;
    
    @Column(name = "episode")
    private int episodeN;
    
    @Column(name = "title")
    private String title;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "foreign_title")
    private Map<LangType, String> foreignTitle;

    @ManyToOne
    private Series series;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Torrent> torrents;

    public Episode() {
    }

    public Episode(int season, int episode, String title) {
        this.seasonN = season;
        this.episodeN = episode;
        this.title = title;
    }

    public Episode(int season, int episode) {
        this.seasonN = season;
        this.episodeN = episode;
    }

    public void putForeignTitle(LangType lang, String title) {
        if (getForeignTitle() == null) {
            setForeignTitle(new HashMap<>());
        }
        getForeignTitle().put(lang, title);
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        setSeries(series, true);
    }

    public void setSeries(Series series, boolean addToSeries) {
        if (this.series != null) {
            this.series.removeEpisode(this, false);
        }
        if (series == null) {
            this.series = null;
            return;
        }
        this.series = series;
        if (addToSeries) {
            this.series.addEpisode(this);
        }
    }

    public void addTorrent(Torrent torrent) {
        if (torrent == null) {
            throw new IllegalArgumentException("Torrent can not be null");
        }
        if (torrents == null) {
            torrents = new HashSet<>();
        }
        torrent.setEpisode(this, false);
        torrents.add(torrent);
    }
    
    public void removeTorrent(Torrent torrent) {
        removeTorrent(torrent, true);
    }

    public void removeTorrent(Torrent torrent, boolean editEpisode) {
        if (torrent == null) {
            throw new IllegalArgumentException("Torrent can not be null");
        }
        if (torrents == null) {
            torrents = new HashSet<>();
            return;
        }
        if (editEpisode) {
            torrent.setEpisode(null, false);
        }
        torrents.remove(torrent);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.seasonN;
        hash = 89 * hash + this.episodeN;
        hash = 89 * hash + Objects.hashCode(this.series);
        return hash;
    }

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
        final Episode other = (Episode) obj;
        if (this.seasonN != other.seasonN) {
            return false;
        }
        if (this.episodeN != other.episodeN) {
            return false;
        }
        if (!Objects.equals(this.series, other.series)) {
            return false;
        }
        return true;
    }

//    GENERATED GETTERS AND SETTERS
//    ------------------------------------------------------------------------
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSeasonN() {
        return seasonN;
    }

    public void setSeasonN(int seasonN) {
        this.seasonN = seasonN;
    }

    public int getEpisodeN() {
        return episodeN;
    }

    public void setEpisodeN(int episodeN) {
        this.episodeN = episodeN;
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

    public Set<Torrent> getTorrents() {
        return torrents;
    }

    public void setTorrents(Set<Torrent> torrents) {
        this.torrents = torrents;
    }

}
