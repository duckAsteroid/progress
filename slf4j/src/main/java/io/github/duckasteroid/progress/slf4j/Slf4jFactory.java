package io.github.duckasteroid.progress.slf4j;

import io.github.duckasteroid.progress.Configuration;
import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListenerFactory;
import io.github.duckasteroid.progress.base.event.ProgressUpdateType;
import io.github.duckasteroid.progress.base.format.CompoundFormat;
import io.github.duckasteroid.progress.base.format.ProgressFormat;
import io.github.duckasteroid.progress.base.format.SimpleProgressFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Slf4jFactory implements ProgressMonitorListenerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(Slf4jFactory.class);

    private static final String NS = "org.duck.asteroid.progress.slf4j.";
    public static final String ROOT_LOGGER_NAME = NS + "root.logger.name";
    private static final int DEFAULT_CACHE_SIZE = 100;
    public static final String CACHE_SIZE = NS + ".cacheSize";
    public static final String FORMAT = NS + ".format";

    private final Map<String, ProgressMonitorListener> cache = new LruCache(getCacheSize());

    private final ProgressFormat format = Configuration.getInstance().getValue(FORMAT, CompoundFormat::parse, SimpleProgressFormat.DEFAULT);

    /**
     * A simple LRU cache based on a {@link LinkedHashMap}
     */
    private static class LruCache extends LinkedHashMap<String, ProgressMonitorListener>
    {
        private final int cacheSize;

        public LruCache(int size) {
            super(size, 0.75f, true);
            this.cacheSize = size;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ProgressMonitorListener> eldest) {
            return size() >= cacheSize;
        }
    }

    /**
     * A map that returns the same value - no matter what key
     */
    private static class SingleLevelMap implements Map<ProgressUpdateType, Slf4JProgress.Level> {
        private final Slf4JProgress.Level value;

        private SingleLevelMap(Slf4JProgress.Level value) {
            this.value = value;
        }

        @Override
        public int size() {
            return ProgressUpdateType.values().length;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return key != null && key instanceof ProgressUpdateType;
        }

        @Override
        public boolean containsValue(Object value) {
            return this.value.equals(value);
        }

        @Override
        public Slf4JProgress.Level get(Object key) {
            return value;
        }

        @Override
        public Slf4JProgress.Level put(ProgressUpdateType key, Slf4JProgress.Level value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Slf4JProgress.Level remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends ProgressUpdateType, ? extends Slf4JProgress.Level> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<ProgressUpdateType> keySet() {
            return Set.of(ProgressUpdateType.values());
        }

        @Override
        public Collection<Slf4JProgress.Level> values() {
            return Collections.singleton(value);
        }

        @Override
        public Set<Entry<ProgressUpdateType, Slf4JProgress.Level>> entrySet() {
            return keySet().stream().map(k -> new Entry<ProgressUpdateType, Slf4JProgress.Level>() {
                @Override
                public ProgressUpdateType getKey() {
                    return k;
                }

                @Override
                public Slf4JProgress.Level getValue() {
                    return value;
                }

                @Override
                public Slf4JProgress.Level setValue(Slf4JProgress.Level value) {
                    throw new UnsupportedOperationException();
                }
            }).collect(Collectors.toSet());
        }
    }

    public String getRootLoggerName() {
        return System.getProperty(ROOT_LOGGER_NAME, ProgressMonitor.class.getName());
    }

    public Logger logger(String name) {
        return LoggerFactory.getLogger(getRootLoggerName() + "." + name);
    }

    public int getCacheSize() {
        int cacheSize = DEFAULT_CACHE_SIZE;
        try {
            String value = System.getProperty(CACHE_SIZE);
            if (value != null) {
                cacheSize = Integer.parseInt(value);
            }
        }
        finally {
            LOG.warn("Unable to read property "+CACHE_SIZE+"="+System.getProperty(CACHE_SIZE));
        }
        return cacheSize;
    }

    public Map<ProgressUpdateType, Slf4JProgress.Level> levels(String name) {
        // FIXME handle custom logging level for events by name
        return new SingleLevelMap(Slf4JProgress.Level.DEBUG);
    }

    public ProgressFormat format(String name) {
        // FIXME handle custom progress format by name
        return format;
    }

    @Override
    public ProgressMonitorListener createProgressMonitorListener(String name) {
        if (!cache.containsKey(name)) {
            Slf4JProgress progress = new Slf4JProgress(logger(name), format(name), levels(name));
            cache.put(name, progress);
            return progress; // don't use cache in case it gets evicted between now and get below...
        }
        return cache.get(name);
    }
}
