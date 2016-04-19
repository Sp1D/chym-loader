/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.service;

import com.omertron.themoviedbapi.MovieDbException;
import java.io.IOException;
import java.util.List;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.prototype.Fetcher;
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
public class FetcherService {
    
    private static final Logger LOG = LogManager.getLogger(FetcherService.class);
    
    @Autowired
    Fetcher tmdbFetcher;
    
    public void fetchSeries(List<Series> series) throws MovieDbException, IOException, MethodNotSupportedException {
        LOG.info("Fetching series descriptions");        
        tmdbFetcher.fetchSeries(series);
    }
    
}
