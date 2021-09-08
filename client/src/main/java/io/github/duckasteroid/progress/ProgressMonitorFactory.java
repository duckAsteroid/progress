package io.github.duckasteroid.progress;

import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListenerFactory;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * A factory for creating {@link ProgressMonitor} instances where we don't care which listeners we
 * get.
 * Each monitor will share the same {@link ProgressMonitorListener}s - as discovered by
 * {@link ServiceLoader} discovery.
 */
public class ProgressMonitorFactory {
  private static final long DEFAULT_SIZE = 100;

  private static final String FACTORY_DEBUG =
      "io.github.duckasteroid.progress.ProgressMonitorFactory.debug";
  private static final boolean DEFAULT_FACTORY_DEBUG = false;

  private static List<ProgressMonitorListener> listeners = fromServiceLoader();


  private static boolean debug = Configuration.getInstance().getBoolean(
      FACTORY_DEBUG, DEFAULT_FACTORY_DEBUG);

  private static List<ProgressMonitorListener> fromServiceLoader() {
    ServiceLoader<ProgressMonitorListener> listenerServiceLoader =
        ServiceLoader.load(ProgressMonitorListener.class);
    List<ProgressMonitorListener> tmp =
        listenerServiceLoader.stream().map(p -> p.get()).collect(Collectors.toList());
    if (tmp.isEmpty() && debug) {
      System.err.println("WARN: No ProgressMonitorListeners found via SPI");
    }
    return new CopyOnWriteArrayList<>(tmp);
  }

  public static void addListener(ProgressMonitorListener listener) {
    listeners.add(listener);
  }

  public static void removeListener(ProgressMonitorListener listener) {
    listeners.remove(listener);
  }

  public static void clearListeners() {
    listeners.clear();
  }

  public static void resetListeners() {
    listeners = fromServiceLoader();
  }

  /**
   * Create a new progress monitor using system wide settings.
   * @param name the name of the monitor
   * @param size the amount of work in the new monitor
   * @return the new monitor (never null)
   */
  public static final ProgressMonitor newMonitor(String name, long size) {
    BaseProgressMonitor monitor = new BaseProgressMonitor(name, size, listeners);
    ServiceLoader.load(ProgressMonitorListenerFactory.class).stream()
        .map(ServiceLoader.Provider::get)
        .map(fac -> fac.createProgressMonitorListener(name))
        .forEach(monitor::addProgressMonitorListener);
    return monitor;
  }

  /**
   * Create a new progress monitor using system wide settings and a default size (100).
   * @param name the name of the monitor
   * @return the new monitor (never null)
   */
  public static final ProgressMonitor newMonitor(String name) {
    return newMonitor(name, DEFAULT_SIZE);
  }
}
