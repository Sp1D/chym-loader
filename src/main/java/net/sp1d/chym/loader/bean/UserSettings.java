/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Sp1D
 */
@Entity
public class UserSettings implements Serializable {
    private static final long serialVersionUID = 1L;    
    
    @Id
    @GeneratedValue    
    private long id;
    
    @Enumerated(EnumType.STRING)
    private SortOrder sortOrder;
    
    private int pageSize;
    
    private boolean favoritesFirst;
    
//    GENERATED GETTERS AND SETTERS

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }       

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isFavoritesFirst() {
        return favoritesFirst;
    }

    public void setFavoritesFirst(boolean favoritesFirst) {
        this.favoritesFirst = favoritesFirst;
    }
    
    
}
