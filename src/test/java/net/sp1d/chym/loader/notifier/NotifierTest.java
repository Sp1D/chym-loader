/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.notifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.ImdbRating;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.User;
import net.sp1d.chym.loader.service.UserService;
import net.sp1d.chym.loader.type.LangType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Sp1D
 */
public class NotifierTest {

    static Updates updates;
    static UserService userService;
    static Properties smtpProperties;
    
    static List<Series> fakeSeries;
    static Map<Series, Set<Episode>> fakeEpisodes;

    public NotifierTest() {
    }

    private static List<Series> getFakeSeries(){
        if (fakeSeries != null) {
            return fakeSeries;
        }
        
        List<Series> listSer = new ArrayList<>();

        Series ser1 = new Series();
        ser1.setTitle("Vikings");
        ser1.putForeignTitle(LangType.RU, "Викинги");
        ser1.setDescription("Series about vikings");
        ser1.putForeignDesc(LangType.RU, "Сериал о викингах");
        ImdbRating ir = new ImdbRating();
        ir.setRating(8.6f);
        ir.setVotes(168000);
        ser1.setImdbRating(ir);
        ser1.setStartDate("03-03-2013");

        Series ser2 = new Series();
        ser2.setTitle("Lost");
        

        listSer.addAll(Arrays.asList(ser1, ser2));
        
        fakeSeries = listSer;
        return fakeSeries;
    }
    
    private static Map<Series, Set<Episode>> getFakeEpisodes(){
        if (fakeEpisodes != null) {
            return fakeEpisodes;                    
        }
        
        Set<Episode> episodes = new HashSet<>();
        Map<Series, Set<Episode>> map = new HashMap<>();
        
        Episode e1 = new Episode();
        e1.setSeasonN(1);
        e1.setEpisodeN(1);
        e1.setSeries(getFakeSeries().get(0));
        e1.setTitle("Rites of Passage");
        e1.putForeignTitle(LangType.RU, "Обряд посвящения");
        
        Episode e2 = new Episode();
        e2.setSeasonN(1);
        e2.setEpisodeN(2);
        e2.setSeries(getFakeSeries().get(0));
        e2.setTitle("Wrath of Northmen");
        e2.putForeignTitle(LangType.RU, "Гнев северян");
        
        episodes.addAll(Arrays.asList(e1, e2));
        
        map.put(getFakeSeries().get(0), episodes);
        
        fakeEpisodes = map;
        return fakeEpisodes;
    }
    
    private static List<User> getFakeUsers(){
        List<User> users = new ArrayList<>();
        
        User u1 = new User();
        u1.setEmail("buzzband@gmail.com");
        u1.setFavorites(new ArrayList<>(getFakeSeries()));
        u1.setUsername("Sp1D");
        u1.setNotifyTypes(new HashSet<>(Arrays.asList(NotifyType.NEWSERIES, NotifyType.NEWEPISODE)));
        u1.setNotifyDeliveryTypes(new HashSet<>(Arrays.asList(NotifyDeliveryType.EMAIL)));
        
        User u2 = new User();
        u2.setEmail("tester@test.test");
        u2.setUsername("Tester");
        u2.setNotifyTypes(new HashSet<>(Arrays.asList(NotifyType.NEWSERIES)));
        u2.setNotifyDeliveryTypes(new HashSet<>(Arrays.asList(NotifyDeliveryType.EMAIL)));
        
        users.addAll(Arrays.asList(u1, u2));
        
        return users;
    }
    
    @BeforeClass
    public static void setUpClass() {
        

        updates = mock(Updates.class);
        when(updates.getSeries()).thenReturn(getFakeSeries());
        when(updates.getEpisodes()).thenReturn(getFakeEpisodes());
        

        userService = mock(UserService.class);
        when(userService.findByNotifySeries()).thenReturn(getFakeUsers());
        when(userService.findUserByFavoriteSeries(any(Series.class))).thenReturn(getFakeUsers());
        
        smtpProperties = new Properties();
        smtpProperties.setProperty("mail.smtp.starttls.enable", "false");
        smtpProperties.setProperty("mail.smtp.auth", "false");
        smtpProperties.setProperty("mail.smtp.host", "localhost");
        smtpProperties.setProperty("mail.smtp.port", "25");

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of notifyUsers method, of class Notifier.
     */
    @Test
    public void testNotifyUsers() throws IOException {
        System.out.println("notifyUsers");
        Notifier instance = new Notifier();
        instance.setUpdates(updates);
        instance.setUserService(userService);
        instance.setSmtpProperties(smtpProperties);
        instance.notifyUsers();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

}
