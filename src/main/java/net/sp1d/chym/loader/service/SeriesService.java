/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.service;

import com.omertron.themoviedbapi.MovieDbException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.Torrent;
import net.sp1d.chym.loader.prototype.Fetcher;
import net.sp1d.chym.loader.prototype.Tracker;
import net.sp1d.chym.loader.repo.SeriesRepo;
import org.apache.http.MethodNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sp1D
 */
@Component
@Service
public class SeriesService {

    private static final Logger LOG = LogManager.getLogger(SeriesService.class);

    @Autowired
    SeriesRepo seriesRepo;

     
    
    public Series getSeriesByTitle(String title){
        Series series = null;
        List<Series> allSeries = seriesRepo.findAll();
        for (Series ser : allSeries) {
            if (ser.getTitle().equalsIgnoreCase(title)) {
                series = ser;
                break;
            }
        }
        return series;
    }
    
    public Series findSeriesByTitle(String title) {
        return seriesRepo.findByTitle(title);
    }
}
