package org.duck.asteroid.progress;

import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;
import org.duck.asteroid.progress.base.event.ProgressMonitorListenerFactory;

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
    private static List<ProgressMonitorListener> listeners = fromServiceLoader();

    private static List<ProgressMonitorListener> fromServiceLoader() {
        ServiceLoader<ProgressMonitorListener> listenerServiceLoader = ServiceLoader.load(ProgressMonitorListener.class);
        List<ProgressMonitorListener> tmp = listenerServiceLoader.stream().map(p -> p.get()).collect(Collectors.toList());
        if (tmp.isEmpty()) {
            System.err.println("Bad configuration - no ProgressMonitorListeners found");
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

    public static final ProgressMonitor newMonitor(String name, long size) {
        BaseProgressMonitor monitor = new BaseProgressMonitor(name, size, listeners);
        ServiceLoader.load(ProgressMonitorListenerFactory.class).stream()
                .map(ServiceLoader.Provider::get)
                .map(fac -> fac.createProgressMonitorListener(name))
                .forEach(monitor::addProgressMonitorListener);
        return monitor;
    }
}
