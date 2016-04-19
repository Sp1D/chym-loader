/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.repo;

import java.util.List;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sp1D
 */
public interface UserRepo extends JpaRepository<User, Long>{
    
    List<User> findByNotifySeries();
    List<User> findByFavoriteSeries(Series series);
    List<User> findByNotifyEpisodes(Episode episode);
    User findOneByEmail(String email);
    
}
