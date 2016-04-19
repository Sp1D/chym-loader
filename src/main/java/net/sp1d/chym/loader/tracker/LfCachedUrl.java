/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.tracker;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Sp1D
 */
public class LfCachedUrl implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private List<String> content;
    private Long lastUpdate;
    private String url;
    private final long CACHE_INTERVAL;
    
    public boolean isObsolete(){
        return System.currentTimeMillis() - lastUpdate > CACHE_INTERVAL;
    }

            

    public LfCachedUrl(long CACHE_INTERVAL, List<String> content) {
        this.CACHE_INTERVAL = CACHE_INTERVAL;
        this.content = content;
        lastUpdate = System.currentTimeMillis();
    }
    
    public LfCachedUrl(long CACHE_INTERVAL, List<String> content, String url) {
        this(CACHE_INTERVAL, content);
        this.url = url;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.content);
        hash = 89 * hash + Objects.hashCode(this.lastUpdate);
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
        final LfCachedUrl other = (LfCachedUrl) obj;
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (!Objects.equals(this.lastUpdate, other.lastUpdate)) {
            return false;
        }
        return true;
    }

    
//    GENERATED GETTERS AND SETTERS
//    ------------------------------------------------------------------------

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    
}
