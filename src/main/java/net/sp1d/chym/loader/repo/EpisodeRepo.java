/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.repo;

import java.util.List;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sp1D
 */
public interface EpisodeRepo extends JpaRepository<Episode, Long>{
    public List<Episode> findBySeriesAndSeasonNOrderByEpisodeNDesc(Series series, int seasonN);
}
