/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import net.sp1d.chym.loader.notifier.NotifyDeliveryType;
import net.sp1d.chym.loader.notifier.NotifyType;
import net.sp1d.chym.loader.repo.SeriesRepo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sp1D
 */
@Entity
@Table(name = "user")

@NamedQueries({
@NamedQuery(name = "User.findByNotifySeries", query = "SELECT u FROM User u WHERE 'NEWSERIES' MEMBER OF u.notifyTypes"),
@NamedQuery(name = "User.findByFavoriteSeries", query = "SELECT u FROM User u WHERE ?1 MEMBER OF u.favorites"),
@NamedQuery(name = "User.findByNotifyEpisodes", query = "SELECT u FROM User u WHERE (SELECT s FROM Series s WHERE ?1 MEMBER OF s.episodes) MEMBER OF u.favorites")
// "SELECT s FROM Series s WHERE ?1 MEMBER OF s.episodes"
})
public class User implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue    
    private long id;
    
    private String email;
    
    private String username;
    
    private String password;
    
    private boolean enabled;
    
    @ManyToMany(fetch = FetchType.EAGER)    
    private List<Series> favorites;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<NotifyType> notifyTypes;
            
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<NotifyDeliveryType> notifyDeliveryTypes;
    
    @Transient
    private String p1;
    
    @Transient
    private String p2;
    
    private UserSettings settings;
    
//    @Autowired
//    @Transient
//    SeriesRepo seriesRepo;
    
//    GENERATED SETTERS AND GETTERS

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public List<Series> getFavorites() {
//        return seriesRepo.findFavoriteSeriesByUser(this);
        return favorites;
    }

    public void setFavorites(List<Series> favorites) {
        this.favorites = favorites;
    }

    public Set<NotifyType> getNotifyTypes() {
        if (notifyTypes == null) notifyTypes = new HashSet<>();
        return notifyTypes;
    }

    public void setNotifyTypes(Set<NotifyType> notifyTypes) {
        this.notifyTypes = notifyTypes;
    }

    public Set<NotifyDeliveryType> getNotifyDeliveryTypes() {
        if (notifyDeliveryTypes == null) notifyDeliveryTypes = new HashSet<>();
        return notifyDeliveryTypes;
    }

    public void setNotifyDeliveryTypes(Set<NotifyDeliveryType> notifyDeliveryTypes) {
        this.notifyDeliveryTypes = notifyDeliveryTypes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }
    
    
    
}
