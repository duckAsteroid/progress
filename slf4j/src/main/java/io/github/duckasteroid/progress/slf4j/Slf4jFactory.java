package io.github.duckasteroid.progress.slf4j;

import io.github.duckasteroid.progress.Configuration;
import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListenerFactory;
import io.github.duckasteroid.progress.base.event.ProgressUpdateType;
import io.github.duckasteroid.progress.base.format.CompoundFormat;
import io.github.duckasteroid.progress.base.format.ProgressFormat;
import io.github.duckasteroid.progress.base.format.SimpleProgressFormat;
import io.github.duckasteroid.progress.slf4j.util.LruCache;
import io.github.duckasteroid.progress.slf4j.util.SingleLevelMap;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for monitors that route updates to SLF4J logger instances.
 * The factory uses the following configuration parameters:
 * <ul>
 *   <li><pre>io.github.duckasteroid.progress.slf4j.root.logger.name</pre>The name of the root SLF4J
 *   logger used to receive messages when monitor name begins with <pre>#</pre>. Default is
 *   <pre>io.github.duckasteroid.progress.ProgressMonitor</pre></li>
 *   <li><pre>io.github.duckasteroid.progress.slf4j.cacheSize</pre>. How may instances of listeners
 *   to cache (rather than re-create when requested). Default 100.</li>
 *   <li><pre>io.github.duckasteroid.progress.slf4j.format</pre>A format string that is parsed to
 *   create a {@link ProgressFormat} instance used to format monitor updates being sent to loggers.
 *   Default is {@link SimpleProgressFormat#DEFAULT}</li>
 * </ul>
 */
public class Slf4jFactory implements ProgressMonitorListenerFactory {
  private static final Logger LOG = LoggerFactory.getLogger(Slf4jFactory.class);

  private static final String NS = "io.github.duckasteroid.progress.slf4j.";
  public static final String ROOT_LOGGER_NAME = NS + "root.logger.name";
  public static final String CACHE_SIZE = NS + ".cacheSize";
  public static final String FORMAT = NS + ".format"; //NOPMD - case is different
  private static final int DEFAULT_CACHE_SIZE = 100;
  private final transient Map<String, ProgressMonitorListener> cache = new LruCache(getCacheSize());

  private final ProgressFormat format = Configuration.getInstance().getValue(FORMAT, //NOPMD
      CompoundFormat::parse, SimpleProgressFormat.DEFAULT);

  public String getRootLoggerName() {
    return System.getProperty(ROOT_LOGGER_NAME, ProgressMonitor.class.getName());
  }

  public Logger logger(String name) {
    if (name.startsWith("#")) {
      return LoggerFactory.getLogger(getRootLoggerName() + "." + name.substring(1));
    }
    else {
      return LoggerFactory.getLogger(name);
    }
  }

  /**
   * Read cache size from properties.
   *
   * @return the cache size
   */
  public int getCacheSize() {
    // TODO Replace with Configuration usage?
    int cacheSize = DEFAULT_CACHE_SIZE; //NOPMD
    try {
      String value = System.getProperty(CACHE_SIZE);
      if (value != null) {
        cacheSize = Integer.parseInt(value);
      }
    } finally {
      LOG.warn("Unable to read property " + CACHE_SIZE + "=" + System.getProperty(CACHE_SIZE));
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

  /**
   * Used to create a new progress monitor instance that will log directly to an SLF4J logger.
   * @param logger the logger to write events to
   * @param levels a map of the levels for each event type
   * @param format a format to use for log messages
   * @param name the name of the monitor
   * @param size the initial size of the monitor
   * @return a new monitor instance
   */
  public static ProgressMonitor newMonitorLoggingTo(Logger logger,
                                             Map<ProgressUpdateType, Slf4JProgress.Level> levels,
                                             ProgressFormat format,
                                             String name, long size) {
    BaseProgressMonitor monitor = new BaseProgressMonitor(name, size,
      Collections.singleton(new Slf4JProgress(logger, format, levels)));
    return monitor;
  }

  public static ProgressMonitor newMonitorLoggingTo(Logger logger,
                                             Slf4JProgress.Level level,
                                             ProgressFormat format,
                                             String name, long size) {
    return newMonitorLoggingTo(logger, new SingleLevelMap(level), format, name, size);
  }

  public static ProgressMonitor newMonitorLoggingTo(Logger logger,
                                             Slf4JProgress.Level level,
                                             String name, long size) {
    return newMonitorLoggingTo(logger, level, SimpleProgressFormat.DEFAULT, name, size);
  }

  public static ProgressMonitor newMonitorLoggingDebugTo(Logger logger,
                                             String name, long size) {
    return newMonitorLoggingTo(logger, Slf4JProgress.Level.DEBUG, SimpleProgressFormat.DEFAULT, name, size);
  }
}
