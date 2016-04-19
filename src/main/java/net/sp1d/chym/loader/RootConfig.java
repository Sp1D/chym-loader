/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Properties;
import javax.sql.DataSource;

import net.sp1d.chym.loader.prototype.Tracker;
import net.sp1d.chym.loader.service.SeriesService;
import net.sp1d.chym.loader.tracker.LfTracker;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author sp1d
 */
@Configuration
@ComponentScan(basePackages = {"net.sp1d.chym.loader.tracker", "net.sp1d.chym.loader", "net.sp1d.chym.loader.type"})
@EnableJpaRepositories("net.sp1d.chym.loader.repo")
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
@Profile("prod")
public class RootConfig {

    @Autowired
    Environment env;

//    Logger log = LoggerFactory.getLogger(RootConfig.class);   
    @Bean
    DataSource dataSource() {        
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(env.getProperty("db.conn.jdbcUrl"));
        ds.setUsername(env.getProperty("db.conn.user"));
        ds.setPassword(env.getProperty("db.conn.password"));
        ds.setDriverClassName(env.getProperty("db.conn.driverClass"));
        
        return ds;
    }
    
  
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

        HibernateJpaVendorAdapter va = new HibernateJpaVendorAdapter();
        va.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
        va.setGenerateDdl(true);

        emf.setJpaVendorAdapter(va);
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("net.sp1d.chym.loader.bean", "net.sp1d.chym.loader.tracker", "net.sp1d.chym.loader.repo");
        emf.setPersistenceUnitName("net.sp1d.loader.chym_PU");

        Properties properties = new Properties();
        properties.setProperty("hibernate.event.merge.entity_copy_observer", "allow");

        emf.setJpaProperties(properties);

        return emf;
    }

    @Bean
    JpaTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setPersistenceUnitName("net.sp1d.loader.chym_PU");
        return tm;
    }

    
    @Bean
    List<Tracker> trackers(){     
        return new ArrayList<>(Arrays.asList(new LfTracker()));
    }
    
    @Bean
    TheMovieDbApi theMovieDbApi() throws MovieDbException{
        return new TheMovieDbApi(env.getProperty("tmdb.apikey"));
    }
    
    @Bean
    Properties smtpProperties(){
        Properties p = new Properties();
        p.setProperty("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
        p.setProperty("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
        p.setProperty("mail.smtp.host", env.getProperty("mail.smtp.host"));
        p.setProperty("mail.smtp.port", env.getProperty("mail.smtp.port"));        
        return p;        
    }
}
