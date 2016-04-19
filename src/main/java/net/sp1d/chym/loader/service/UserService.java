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
import net.sp1d.chym.loader.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Sp1D
 */
@Service
public class UserService {
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    SeriesService seriesService;
    
    @Transactional
    public User findByEmail(String email) {       
        return userRepo.findOneByEmail(email);
    }
    
    @Transactional
    public List<User> findUserByFavoriteSeries(Series series) {
        return userRepo.findByFavoriteSeries(series);
    }
    
    @Transactional
    public List<User> findByNotifySeries(){
        return userRepo.findByNotifySeries();
    }
    
    @Transactional
    List<User> findByNotifyEpisodes(Episode episode) {
        return userRepo.findByNotifyEpisodes(episode);
    }
    
    @Transactional
    public List<User> findAll() {
        return userRepo.findAll();
    }
    
    public User save(User user) {
        return userRepo.save(user);
    }
}
