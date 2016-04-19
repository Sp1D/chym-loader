/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.repo;

import net.sp1d.chym.loader.bean.Series;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sp1D
 */
public interface SeriesRepo extends JpaRepository<Series, Long>{
    Series findByTitle(String title);
}
