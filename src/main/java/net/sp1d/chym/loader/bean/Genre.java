/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;
import net.sp1d.chym.loader.type.LangType;

/**
 *
 * @author Sp1D
 */
@Entity
@Table(name = "genre")
public class Genre implements Serializable{

    private static final long serialVersionUID = 1L;
        
    @Id    
    @Column(name = "genre_id")
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn
    @Column(name = "foreign_name")
    private Map<LangType, String> foreignName;
    
    public void putForeignName(LangType lang, String name) {
        if (getForeignName() == null) {
            setForeignName(new HashMap<>());
        }
        getForeignName().put(lang, name);
    }
    
    public String readForeignName(LangType lang){
        return getForeignName() != null ? getForeignName().get(lang) : "";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
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
        final Genre other = (Genre) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<LangType, String> getForeignName() {
        return foreignName;
    }

    public void setForeignName(Map<LangType, String> foreignName) {
        this.foreignName = foreignName;
    }
    
    
    
}
