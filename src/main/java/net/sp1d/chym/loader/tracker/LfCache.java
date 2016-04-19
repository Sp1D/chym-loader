/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader.tracker;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sp1D
 */
public class LfCache implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LfCache.class);
    private static final String FILENAME = "urlcache";
    private static final long WRITING_INTERVAL = 2000;
//    private final Thread writerThread;
//    private final CacheWriter cacheWriter;

    private final Map<String, LfCachedUrl> cache;

    public LfCache() {
        cache = new HashMap<>();
        try {
            readSavedCache();
        } catch (ClassNotFoundException | IOException ex) {
            LOG.error("Can not read saved cache because of IO error or internal program error");
        }
    }
    
    public void exit() {
        try {
            saveCache();
        } catch (IOException ex) {
            LOG.error("Error while saving parser's cache", ex);
        }
    }

    private void readSavedCache() throws IOException, ClassNotFoundException {
        if (Files.exists(Paths.get(FILENAME))) {
            LOG.debug("Reading saved parser's cache");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
                while (true) {
                    LfCachedUrl cached = (LfCachedUrl) ois.readObject();
                    String url = cached.getUrl();
                    cache.put(url, cached);
                }
            } catch (EOFException ex) {
                LOG.debug("Reached end of cache file");
            }
        }
    }

    private void saveCache() throws IOException {
        LOG.debug("Saving parser's cache");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            for (LfCachedUrl entry : cache.values()) {
                oos.writeObject(entry);
            }
        }
    }

//    public void run(){
//        writerThread.start();
//    }
//    
//    public void finish(){
//        writerThread.interrupt();
//    }
    public LfCachedUrl addCache(String url, LfCachedUrl cachedUrl) {
//        cacheWriter.Write(cachedUrl);
        return cache.put(url, cachedUrl);
    }

    public LfCachedUrl readCache(String url) {
        return cache.get(url);
    }

    class CacheWriter implements Runnable {

        private Queue<LfCachedUrl> queue;

        public CacheWriter() {
            queue = new LinkedList<>();
        }

        public void Write(LfCachedUrl element) {
            queue.add(element);
        }

        @Override
        public void run() {

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
                while (!Thread.currentThread().isInterrupted()) {
                    LfCachedUrl obj = queue.poll();
                    if (obj != null) {
                        oos.writeObject(obj);
                    }
                    sleep(WRITING_INTERVAL);
                }
            } catch (IOException ex) {
                LOG.error("Error in IO operation", ex);
            } catch (InterruptedException ex) {
                LOG.debug("Thread {} interrupted", Thread.currentThread().getName());
            }

        }

    }

}
