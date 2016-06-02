/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.service;

import net.sp1d.chym.loader.repo.EpisodeRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sp1D
 */
@Service
public class EpisodeService {
    private static final Logger LOG = LogManager.getLogger(EpisodeService.class);

    @Autowired
    protected EpisodeRepo episodeRepo;
    
}
