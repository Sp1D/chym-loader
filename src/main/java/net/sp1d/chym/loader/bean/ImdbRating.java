/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Sp1D
 */
@Embeddable
public class ImdbRating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = true, name = "imdb_rating")
    private float rating;
    
    @Column(nullable = true, name = "imdb_votes")
    private int votes;

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

}
