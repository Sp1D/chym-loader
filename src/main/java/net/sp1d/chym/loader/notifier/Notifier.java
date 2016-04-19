/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.notifier;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.bean.User;
import net.sp1d.chym.loader.service.UserService;
import net.sp1d.chym.loader.type.LangType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sp1D
 */
@Component
public class Notifier {

    private static final Logger LOG = LogManager.getLogger(Notifier.class);

    @Autowired
    private Updates updates;

    @Autowired
    private UserService userService;

    @Autowired
    private Properties smtpProperties;

    private final String EMAIL_SERIES_TMPL = "Привет, пользователь {username}!\n"
            + "Появились новые сериалы, которые, возможно, тебя заинтересуют.\n"
            + "Это:\n"
            + "{newseries}\n"
            + "Спасибо за внимание!";

    private final String EMAIL_EPISODES_TMPL = "Привет, пользователь {username}!\n\n"
            + "Появились новые эпизоды сериала, на который ты подписан:\n"
            + "{newepisodes}\n\n"
            + "Спасибо за внимание!";

    private final String NEWSERIES = "%1$s, вышел на экраны %2$s, IMDB %3$f(%4$d)\n";
    private final String NEWEPISODES = "%1$s (%2$s): S%3$dE%4$d %5$s (%6$s)\n";

    public void notifyUsers() throws IOException {
        LOG.debug("Notifying users about updates");
        if (updates.getSeries() != null && !updates.getSeries().isEmpty()) {
            notifyUsersAboutNewSeries();
        }
        if (updates.getEpisodes() != null && !updates.getEpisodes().isEmpty()) {
            notifyUsersAboutNewEpisodes();
        }
    }

    private void notifyUsersAboutNewSeries() throws IOException {

        List<User> users = userService.findByNotifySeries();

        for (User user : users) {
            if (user.getNotifyDeliveryTypes() != null) {
                for (NotifyDeliveryType delivery : user.getNotifyDeliveryTypes()) {
                    switch (delivery) {
                        case EMAIL:
                            createEmailNewSeries(user, updates.getSeries());
                            break;
                    }

                }
            }
        }
    }

    private void notifyUsersAboutNewEpisodes() throws IOException {

        for (Series series : updates.getEpisodes().keySet()) {
            List<User> users = userService.findUserByFavoriteSeries(series);

            for (User user : users) {
                for (NotifyDeliveryType delivery : user.getNotifyDeliveryTypes()) {
                    switch (delivery) {
                        case EMAIL:
                            createEmailNewEpisodes(NotifyType.NEWEPISODE, user, updates.getEpisodes().get(series));
                            break;
                    }

                }

            }

        }

    }

    private void createEmailNewEpisodes(NotifyType notifyType, User user, Set<Episode> newEpisodes) throws IOException {
        String message = EMAIL_EPISODES_TMPL.replaceAll("\\{username\\}", user.getUsername());

        StringBuilder epiString = new StringBuilder();
        for (Episode epi : newEpisodes) {
//            private final String NEWEPISODES = "S%1$dE%2$d %3$s (%4$s) - %5$s (%6$s)\n";
            epiString.append(String.format(NEWEPISODES, 
                    epi.getSeries().readForeignTitle(LangType.RU), epi.getSeries().getTitle(), 
                    epi.getSeasonN(), epi.getEpisodeN(),
                    epi.getForeignTitle().get(LangType.RU), epi.getTitle() ));
        }
        message = message.replaceAll("\\{newepisodes\\}", epiString.toString());

        sendEmail(user.getEmail(), "New episodes!", message);
    }

    private void createEmailNewSeries(User user, List<Series> newSeries) throws IOException {
        String message = EMAIL_SERIES_TMPL.replaceAll("\\{username\\}", user.getUsername());

        StringBuilder seriesString = new StringBuilder();
        for (Series ser : newSeries) {
//            private final String NEWSERIES = "%1$s, вышел на экраны %2$s, IMDB %3$f(%4$d)";

            float rating = 0f;
            int votes = 0;
            String startDate = "неизвестно когда";
            if (ser.getImdbRating() != null) {
                rating = ser.getImdbRating().getRating();
                votes = ser.getImdbRating().getVotes();
            }
            if (ser.getStartDate() != null) {
                startDate = ser.getStartDate();
            }
            seriesString.append(String.format(NEWSERIES,
                    ser.getTitle(), startDate,
                    rating, votes));
        }
        message = message.replaceAll("\\{newseries\\}", seriesString.toString());

        sendEmail(user.getEmail(), "New series", message);
    }

    private void sendEmail(String address, String subject, String message) throws IOException {
        LOG.debug("Sending email with subject {} to {}", subject, address);

        Session session = Session.getInstance(smtpProperties, null);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom("ihopenobodyelsewantsthisname@gmail.com");
            msg.setRecipients(Message.RecipientType.TO,
                    address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(message);
            Transport.send(msg, "ihopenobodyelsewantsthisname@gmail.com", "VIPower16");

        } catch (MessagingException ex) {
            LOG.error("Error while sending an email message", ex);
        }
    }

    public Updates getUpdates() {
        return updates;
    }

    public void setUpdates(Updates updates) {
        this.updates = updates;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Properties getSmtpProperties() {
        return smtpProperties;
    }

    public void setSmtpProperties(Properties smtpProperties) {
        this.smtpProperties = smtpProperties;
    }

}
