/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.prototype;

/**
 *
 * @author Sp1D
 */
public abstract class AbstractTracker {
    protected static String name;
    protected static String website;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        AbstractTracker.name = name;
    }

    public static String getWebsite() {
        return website;
    }

    public static void setWebsite(String website) {
        AbstractTracker.website = website;
    }

   
    
    
}
