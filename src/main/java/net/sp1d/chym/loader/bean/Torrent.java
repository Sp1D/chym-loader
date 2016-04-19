/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Sp1D
 */
@Entity
@Table(name = "torrent")
public class Torrent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "torrent_id")
    private long id;

//    @Enumerated(EnumType.STRING)
//    private Quality quality;
    
    private String quality;

    private String torrent;
    private String magnet;

    @ManyToOne
    private Episode episode;
    
    @Column(name = "tracker_name")
    private String trackerName;

    public Episode getEpisode() {
        return episode;
    }
    
    public void setEpisode(Episode episode) {
        setEpisode(episode, true);
    }

    public void setEpisode(Episode episode, boolean addToEpisode) {
        if (this.episode != null) {
            this.episode.removeTorrent(this, false);
        }
        if (episode == null) {
            this.episode = null;
            return;
        }
        this.episode = episode;
        if (addToEpisode) {
            this.episode.addTorrent(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.quality);
        hash = 47 * hash + Objects.hashCode(this.torrent);
        hash = 47 * hash + Objects.hashCode(this.magnet);
        hash = 47 * hash + Objects.hashCode(this.episode);
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
        final Torrent other = (Torrent) obj;
        if (!Objects.equals(this.torrent, other.torrent)) {
            return false;
        }
        if (!Objects.equals(this.magnet, other.magnet)) {
            return false;
        }
        if (this.quality != other.quality) {
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

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    

    public String getTorrent() {
        return torrent;
    }

    public void setTorrent(String torrent) {
        this.torrent = torrent;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

   
    

}
