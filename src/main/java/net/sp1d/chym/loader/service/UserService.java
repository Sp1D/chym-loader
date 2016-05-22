/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.User;
import net.sp1d.chym.loader.notifier.NotifyDeliveryType;
import net.sp1d.chym.loader.notifier.NotifyType;
import net.sp1d.chym.loader.repo.SeriesRepo;
import net.sp1d.chym.loader.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Sp1D
 */
@Service
public class UserService {
    
    @Autowired
    protected UserRepo userRepo;
    
    @Autowired SeriesRepo seriesRepo;
    
    @Autowired
    protected SeriesService seriesService;
            
    public User findByEmail(String email) {       
        return userRepo.findOneByEmail(email);
    }
   
    public List<User> findUserByFavoriteSeries(Series series) {
        return userRepo.findByFavoriteSeries(series);
    }
   
    public List<User> findByNotifySeries(){
        return userRepo.findByNotifySeries();
    }
    
    List<User> findByNotifyEpisodes(Episode episode) {
        return userRepo.findByNotifyEpisodes(episode);
    }    
    
    public List<User> findAll() {
        return userRepo.findAll();
    }
    
    public User saveAndFlush(User user){
        return userRepo.saveAndFlush(user);
    }
    
    public List<Series> findFavoriteSeries(User user) {
        return seriesRepo.findFavoriteSeriesByUser(user);
    }

//    public void initLazyCollections(User user){
//        
//    }
}
