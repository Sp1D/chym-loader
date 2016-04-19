/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.tracker;

import com.google.api.client.http.*;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sp1d.chym.loader.bean.Series;
import net.sp1d.chym.loader.prototype.Parser;
import net.sp1d.chym.loader.type.IdType;
import org.springframework.stereotype.Component;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sp1d.chym.loader.bean.Episode;
import net.sp1d.chym.loader.bean.Torrent;
import net.sp1d.chym.loader.bean.TrackerSpecificForSeries;
import net.sp1d.chym.loader.service.SeriesService;
import net.sp1d.chym.loader.type.LangType;
import net.sp1d.chym.loader.type.TrackerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author che
 */
@Component
public class LfParser implements Parser {

    private static final Logger LOG = LogManager.getLogger(LfParser.class);

    private final HttpRequestFactory reqF = new NetHttpTransport().createRequestFactory();
    
    static final String NAME = "LostfilmParserN1";
    private final String BASE_URL = "http://www.lostfilm.tv";
    private final String SHOWS_PAGE = "http://www.lostfilm.tv/serials.php";
    private final String RSS_URL = "http://www.lostfilm.tv/rssdd.xml";
    private final String SHOW_BASE_URL = "http://www.lostfilm.tv/browse.php?cat=";
    private final String UID_COOKIE = "1542955";
    private final String PASS_COOKIE = "83cc1f785c24263d300d03ca9545b4cc";
    private final long CACHE_INTERVAL = 120_000;
    private LfCache cache;
    private DocumentBuilderFactory dbf;

    @Autowired
    SeriesService seriesService;

    @Override
    public void exit() {
        if (cache != null) {
            cache.exit();
        }
    }

    private List<String> getPageContent(String stringUrl) throws MalformedURLException, IOException {
        if (cache == null) {
            cache = new LfCache();
        }
        LfCachedUrl cachedUrl = cache.readCache(stringUrl);
        if (cachedUrl != null) {
            if (!cachedUrl.isObsolete()) {
                LOG.debug("Got cached page {}", stringUrl);
                return cachedUrl.getContent();
            } else {
                LOG.debug("Retrieving from web, because cache is obsolete {}", stringUrl);
            }
        }

        List<String> strings = new LinkedList<>();
        GenericUrl gUrl;

        gUrl = new GenericUrl(new URL(stringUrl));

        HttpRequest req = reqF.buildGetRequest(gUrl);
        HttpResponse responce = req.execute();

        if (responce.getStatusCode() >= 300) {
            throw new IOException("HTTP error, status: " + responce.getStatusCode()
                    + ", " + responce.getStatusMessage());
        }
        Charset charset = responce.getContentCharset();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responce.getContent(), charset))) {
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    strings.add(line);
                }
            } while (line != null);
        }

        cache.addCache(stringUrl, new LfCachedUrl(CACHE_INTERVAL, strings, stringUrl));
        return strings;
    }

    private String parseYearByLocalId(String localId) throws IOException {
        String year = null;
        Pattern yearPattern = Pattern.compile("\\u0413\\u043E\\u0434\\s+\\u0432\\u044B\\u0445\\u043E\\u0434\\u0430:\\s*<span>(\\d{4})<\\/span><br\\s+\\/>");
        for (String line : getPageContent(SHOW_BASE_URL + localId)) {
            Matcher matcher = yearPattern.matcher(line);
            if (matcher.find()) {
                year = matcher.group(1);
                break;
            }
        }
        return year;
    }

    

 
    private String getRssContent() throws IOException {
        GenericUrl url = new GenericUrl(RSS_URL);
        HttpRequest request = null;
        HttpResponse responce = null;
        String feedContent = null;
        try {
            request = reqF.buildGetRequest(url);
            responce = request.execute();
            feedContent = responce.parseAsString();
            responce.disconnect();
        } catch (IOException ex) {
            LOG.error("Error while reading RSS");
            throw ex;
        }
        return feedContent;
    }

    private Map<String, Date> parseRssDates() throws IOException {
        if (dbf == null) {
            dbf = DocumentBuilderFactory.newInstance();
        }

        DocumentBuilder db;
        Document rss;
        NodeList itemList;
        URL url;
        try {
            url = new URL(RSS_URL);
        } catch (MalformedURLException ex) {
            throw new IOException(ex);
        }
        Map<String, Date> updatesMap = new HashMap<>();

        try (InputStream urlStream = url.openStream();) {
            db = dbf.newDocumentBuilder();
            rss = db.parse(urlStream);

            Pattern titlePat = Pattern.compile(".+?\\((.+?)\\)\\..*");
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

            itemList = rss.getElementsByTagName("item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Node item = itemList.item(i);

                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;
                    String title;

                    Matcher titleMat = titlePat.matcher(element.getElementsByTagName("title").item(0).getTextContent());
                    if (titleMat.find()) {
                        title = titleMat.group(1);
                    } else {
                        continue;
                    }

                    Date pubDate = sdf.parse(element.getElementsByTagName("pubDate").item(0).getTextContent());

                    Date lastDate = updatesMap.get(title);
                    if (lastDate == null) {
                        lastDate = pubDate;
                    } else if (pubDate.after(lastDate)) {
                        lastDate = pubDate;
                    }

                    updatesMap.put(title, lastDate);
                    LOG.debug("RSS says about {}, latest update at {}", title, lastDate);
                }
            }
        } catch (ParserConfigurationException | SAXException | ParseException ex) {
            LOG.error("Error while parsing RSS XML");
            throw new IOException("Error while parsing RSS XML", ex);
        }
        return updatesMap;
    }

    public List<Series> parseUpdatedSeriesFromRss(List<Series> allSeries) throws IOException {
        if (allSeries == null) {
            throw new IllegalArgumentException("List of all series can not be null");
        }

        Map<String, Date> updatesMap = parseRssDates();

        List<Series> updatedSeries = new ArrayList<>();

        for (String newTitle : updatesMap.keySet()) {
            Series upd = seriesService.findSeriesByTitle(newTitle);
            if (upd == null) {
                String newTitleId = parseIdByTitle(newTitle);
                for (Series ser : allSeries) {
                    if (ser.readExtId(IdType.LOSTFILM).equals(newTitleId)) {
                        upd = ser;
                        break;
                    }
                }
            }
            if (upd != null) {
                Date lastUpd;
                if (upd.readTrackerSpecific(TrackerType.LOSTFILM) != null
                        && upd.readTrackerSpecific(TrackerType.LOSTFILM).getRssPubDate() != null) {
                    lastUpd = upd.readTrackerSpecific(TrackerType.LOSTFILM).getRssPubDate();
                } else {
                    lastUpd = new Date(1L);
                }

                if (updatesMap.get(newTitle).after(lastUpd)) {
                    TrackerSpecificForSeries tsfs = upd.readTrackerSpecific(TrackerType.LOSTFILM);
                    tsfs.setRssPubDate(updatesMap.get(newTitle));
                    upd.putTrackerSpecific(TrackerType.LOSTFILM, tsfs);
                    updatedSeries.add(upd);
                    LOG.debug("Series {} need to renew", upd.getTitle());
                }
            }
        }
//        urlStream.close();
        return updatedSeries;

    }

    public String parseRssLastBuildDate() throws IOException {
        String lastBuildDate = null;
        Pattern lastBuildDatePattern = Pattern.compile("<lastBuildDate>(.*)<\\/lastBuildDate>");

        String feedContent = getRssContent();
        Matcher matcher = lastBuildDatePattern.matcher(feedContent);
        if (matcher.find()) {
            lastBuildDate = matcher.group(1);
        }
        return lastBuildDate;
    }

    
    @Override
    public List<Series> loadSeries() throws IOException {
        LOG.debug("Loading all series from tracker");
        Long startMillis = System.currentTimeMillis();

        List<Series> series = new LinkedList<>();
        List<String> page = new LinkedList<>();
        GenericUrl gUrl;

        gUrl = new GenericUrl(new URL(SHOWS_PAGE));

        HttpRequest req = reqF.buildGetRequest(gUrl);
        HttpResponse responce = req.execute();

        if (responce.getStatusCode() >= 300) {
            throw new IOException("HTTP error, status: " + responce.getStatusCode()
                    + ", " + responce.getStatusMessage());
        }
        Charset charset = responce.getContentCharset();

        BufferedReader br = new BufferedReader(new InputStreamReader(responce.getContent(), charset));
        String line;
        Boolean copy = false;
        do {
            line = br.readLine();
            if (!copy && line.contains("<div class=\"bb\">")) {
                copy = true;
            }
            if (copy && line.contains("</div>")) {
                break;
            }
            if (copy) {
                page.add(line);
            }
        } while (line != null || "".equals(line));
        br.close();

        responce.disconnect();
        Pattern idAndTitlePattern = Pattern.compile("<a\\shref=\"\\/browse\\.php\\?cat="
                + "(?<id>\\d+)\".+<br><span>\\("
                + "(?<title>.+)\\)<\\/span><\\/a>");

        String title;
        for (String l : page) {
            Matcher m = idAndTitlePattern.matcher(l);

            if (m.find()) {
                title = new String(m.group("title").getBytes("utf-8"));
                Series s = new Series();
                s.setTitle(title);

                TrackerSpecificForSeries tsfs = new TrackerSpecificForSeries();
                tsfs.setTitleOnTracker(title);
                s.putTrackerSpecific(TrackerType.LOSTFILM, tsfs);

                s.putExtId(IdType.LOSTFILM, m.group("id"));
                try {
                    s.setStartYear(Integer.valueOf(parseYearByLocalId(m.group("id"))));
                } catch (NumberFormatException ex) {
                    s.setStartYear(-1);
                }

                series.add(s);

            }

        }

        LOG.debug("Running loadSeries() lasts {} ms", (System.currentTimeMillis() - startMillis));
        return series;
    }

    private String parseIdByTitle(String title) throws IOException {
        List<String> page = getPageContent(SHOWS_PAGE);

        Pattern idAndTitlePattern = Pattern.compile("<a\\shref=\"\\/browse\\.php\\?cat="
                + "(?<id>\\d+)\".+<br><span>\\("
                + "(?<title>.+)\\)<\\/span><\\/a>");

        for (String s : page) {
            Matcher m = idAndTitlePattern.matcher(s);

            if (m.find()) {
                String foundTitle = new String(m.group("title").getBytes("utf-8"));
                if (foundTitle.equalsIgnoreCase(title)) {
                    return m.group("id");
                }
            }
        }
        return null;
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Находит эпизоды, перечисленные на странице
     * http://www.lostfilm.tv/browse.php?cat= Создает соответствующие объекты
     * Episode, с заданными полями номера сезона, эпизода, названием серии на
     * английском и русском языках
     *
     * @param series
     * @return List of Episode
     * @throws IOException
     */
    private Set<Episode> parseNewEpisodesBySeries(Series series) throws IOException {
        LOG.debug("Parsing episodes by series from Lostfilm #{} \"{}\"", series.readExtId(IdType.LOSTFILM), series.getTitle());
        Set<Episode> newEpisodes = new HashSet<>();

        Pattern seasonAndEpisodePat = Pattern.compile("<td class=\"\"><span class=\"micro\" style=\"color:gray;\"><span>.*<\\/span>, <span>(?<season>\\d+) \\u0441\\u0435\\u0437\\u043e\\u043d (?<episode>\\d+) \\u0441\\u0435\\u0440\\u0438\\u044f<\\/span><\\/td>");
        Pattern titlePat = Pattern.compile("<div id=\"TitleDiv.*\"><nobr><span style=\"color:#4b4b4b\">(?<rustitle>.+)<\\/span><br \\/>\\((?<title>.+)\\).*<\\/nobr><\\/div><\\/div><\\/td>");

        List<String> page = getPageContent(SHOW_BASE_URL + series.readExtId(IdType.LOSTFILM));
        Matcher m;
        String line;
        int index;
        ListIterator<String> mainIterator = page.listIterator();
        while (mainIterator.hasNext()) {
            index = mainIterator.nextIndex();
            line = mainIterator.next();
            m = seasonAndEpisodePat.matcher(line);
            if (m.find()) {
                int seasonNum = Integer.valueOf(m.group("season"));
                int episodeNum = Integer.valueOf(m.group("episode"));
                LOG.debug("Found episode {}:{}", seasonNum, episodeNum);
                String title = "";
                String rusTitle = "";

                String titLine;
                ListIterator<String> titIterator = page.listIterator(index);
                while (titIterator.hasNext()) {
                    titLine = titIterator.next();
                    Matcher titMatcher = titlePat.matcher(titLine);
                    if (titMatcher.find()) {
                        title = titMatcher.group("title").trim();
                        rusTitle = titMatcher.group("rustitle").trim();
                        LOG.debug("Found titles for {}:{} - \"{}\"/\"{}\"", seasonNum, episodeNum, title, rusTitle);
                        break;
                    }
                }
                Episode episode = new Episode(seasonNum, episodeNum, title);
                episode.putForeignTitle(LangType.RU, rusTitle);
                if (!series.containsEpisode(episode)) {
                    episode.setSeries(series);
                    newEpisodes.add(episode);
                }
            }
        }

        return newEpisodes;
    }

    /**
     * Загружает список эпизодов указанного сериала
     *
     * @param series
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    public Set<Episode> loadNewEpisodesBySeries(Series series) throws IOException, IllegalArgumentException {
        if (series == null || (!isNumeric(series.readExtId(IdType.LOSTFILM))
                && (series.getTitle() == null || series.getTitle().isEmpty()))) {
            throw new IllegalArgumentException();
        }

        if (series.readExtId(IdType.LOSTFILM) == null) {
            series.putExtId(IdType.LOSTFILM, parseIdByTitle(series.getTitle()));
        }
        return parseNewEpisodesBySeries(series);
    }

    /**
     * Загружает список ссылок на торрент-файлы (или magnet-ссылки) указанного
     * эпизода
     *
     * @param episode
     * @return
     */
    @Override
    public List<Torrent> loadTorrentsByEpisode(Episode episode) throws IOException {
        return parseTorrentsByEpisode(episode);
    }

    private List<Torrent> parseTorrentsByEpisode(Episode episode) throws IOException {
        LOG.debug("Parsing torrents for episode S{} E{} \"{}\"", episode.getSeasonN(), episode.getEpisodeN(), episode.getSeries().getTitle());
        String id = episode.getSeries().getExtId().get(IdType.LOSTFILM);
        int seasonN = episode.getSeasonN();
        int episodeN = episode.getEpisodeN();

        List<Torrent> torrents = new ArrayList<>();

        Pattern redirectToLinkPattern = Pattern.compile("location\\.replace\\(\"(?<link>.+)\"\\);");

        StringBuffer q = new StringBuffer("");
        for (LfQuality quality : LfQuality.values()) {
            q = q.append(quality.getPattern());
            q = q.append("|");
        }

        q = q.deleteCharAt(q.length() - 1);
        Pattern linkAndQualityPattern = Pattern.compile("<a\\s+href=\""
                + "(?<link>.+)\"\\sstyle=\"font-size:18px;font-weight:bold;\">.*?(?<quality>" + q + ")<\\/a><br\\s*\\/>");

        String transitLink = String.format("http://www.lostfilm.tv/nrdr2.php?c=%s&s=%s&e=%s", id, seasonN, episodeN);

        GenericUrl transitUrl = new GenericUrl(transitLink);
        HttpRequest transitRequest = reqF.buildGetRequest(transitUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setCookie("uid=" + UID_COOKIE + "; pass=" + PASS_COOKIE);
        transitRequest.setHeaders(headers);
        transitRequest.setFollowRedirects(true);
        HttpResponse transitResponce = transitRequest.execute();

        if (transitResponce.getStatusCode() >= 300) {
            throw new IOException("HTTP error, status: " + transitResponce.getStatusCode()
                    + ", " + transitResponce.getStatusMessage());
        }

        boolean wasFound = false;
        String retreLink;
        String respString;
        Matcher redirectLink = redirectToLinkPattern.matcher(transitResponce.parseAsString());
        transitResponce.disconnect();
        if (redirectLink.find()) {
            retreLink = redirectLink.group("link");
            GenericUrl retreUrl = new GenericUrl(retreLink);
            HttpRequest retreRequest = reqF.buildGetRequest(retreUrl);
            retreRequest.setHeaders(headers);

            HttpResponse retreResponse = retreRequest.execute();
            respString = retreResponse.parseAsString();
            retreResponse.disconnect();

            if (!respString.isEmpty()) {
                Matcher linkAndQ = linkAndQualityPattern.matcher(respString);
                if (!linkAndQ.find()) {


                    wasFound = false;

                }
                linkAndQ.reset();
                while (linkAndQ.find()) {
                    Torrent t = new Torrent();
                    t.setTorrent(linkAndQ.group("link"));
//                    t.setLocalId(id);
                    LfQuality quality = LfQuality.find(linkAndQ.group("quality"));
                    t.setQuality(quality.toString());
                    t.setTrackerName(LfTracker.getName());
//                    t.setTracker(getTracker());
                    t.setEpisode(episode);

//                    t.setSeason(seasonN);
//                    t.setType(Type.EPISODE);
//                    t.setPartially(false);
//                    t.setTitle(title);
                    torrents.add(t);
                    wasFound = true;
                    LOG.debug("Found torrent {} {} {}", t.getTrackerName(), t.getQuality(), t.getTorrent());
                }
            }
        } else {
            throw new IOException("Link not found at " + transitLink);
        }
//        return wasFound;
        return torrents;
    }


    @Override
    public List<Series> getUpdatedSeriesByRss(List<Series> allSeries) throws IOException {
        return parseUpdatedSeriesFromRss(allSeries);
    }

}
