/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.fetcher;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sp1d.chym.loader.bean.ImdbRating;
import org.apache.http.MethodNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sp1D
 */
@Component
public class ImdbFetcher {

    private static final Logger LOG = LogManager.getLogger(ImdbFetcher.class);

    private final HttpRequestFactory reqF = new NetHttpTransport().createRequestFactory();

    private final String IMDB_PATH = "http://www.imdb.com/title/";
    private final String RATING_PATTERN = "<strong title=\".*\"><span itemprop=\"ratingValue\">(?<rating>.+)<\\/span><\\/strong>";
    private final String VOTES_PATTERN = "<span class=\"small\" itemprop=\"ratingCount\">(?<votes>.+)<\\/span><\\/a>";
    private final Pattern ratingPat;
    private final Pattern votesPat;

    public ImdbFetcher() {
        ratingPat = Pattern.compile(RATING_PATTERN);
        votesPat = Pattern.compile(VOTES_PATTERN);
    }

    public ImdbRating getRating(String imdbId) throws IOException, MethodNotSupportedException {
        if (imdbId == null || imdbId.isEmpty()) {            
            ImdbRating emptyRating = new ImdbRating();
            emptyRating.setRating(0f);
            emptyRating.setVotes(0);
            return emptyRating;
        }
        HttpRequest req = reqF.buildGetRequest(new GenericUrl(URI.create(IMDB_PATH + imdbId)));
        req.setNumberOfRetries(3);

        HttpResponse resp = req.execute();

        String parseSource = resp.parseAsString();
        resp.disconnect();

        if (resp.getStatusCode() < 200 || resp.getStatusCode() >= 300) {
            LOG.error("Error while reading IMDB page {}, status code {}", IMDB_PATH + imdbId, resp.getStatusCode());
            return null;
        }

        ImdbRating rating = new ImdbRating();

        Matcher m = ratingPat.matcher(parseSource);
        if (m.find()) {
            rating.setRating(Float.valueOf(m.group("rating").trim()));
        }

        m = votesPat.matcher(parseSource);
        if (m.find()) {
            String votes = m.group("votes").trim().replace(",", "");
            rating.setVotes(Integer.valueOf(votes));
        }

        return rating;
    }
}
