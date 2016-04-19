/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Sp1D
 */
@Entity
@Table(name = "tsfs")
public class TrackerSpecificForSeries implements Serializable{

    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue
    @Column(name = "tsfs_id")
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rss_pub_date")
    private Date rssPubDate;
    
    @Column(name = "title_ontracker")
    private String titleOnTracker;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getRssPubDate() {
        return rssPubDate;
    }

    public void setRssPubDate(Date rssPubDate) {
        this.rssPubDate = rssPubDate;
    }

    public String getTitleOnTracker() {
        return titleOnTracker;
    }

    public void setTitleOnTracker(String titleOnTracker) {
        this.titleOnTracker = titleOnTracker;
    }
    
    
    
    
}
