/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.prototype;

import com.omertron.themoviedbapi.MovieDbException;
import java.io.IOException;
import java.util.List;
import net.sp1d.chym.loader.bean.Series;
import org.apache.http.MethodNotSupportedException;

/**
 *
 * @author Sp1D
 */
public interface Fetcher {
    public void fetchSeries(List<Series> series) throws MovieDbException, IOException, MethodNotSupportedException;
}
