package org.duck.asteroid.progress.utils;

import org.duck.asteroid.progress.ProgressMonitor;

import java.util.*;
import java.util.function.Consumer;

public class SpliteratorProgress<T> implements Spliterator<T>, AutoCloseable {
    private Spliterator<T> underlying;
    private ProgressMonitor monitor;
    private Set<Spliterator<T>> openChildSpliterators;

    public SpliteratorProgress(Spliterator<T> underlying, ProgressMonitor monitor) {
        this(underlying, monitor, Collections.synchronizedSet(new HashSet<>())); // has to be synchronized
    }

    private SpliteratorProgress(Spliterator<T> underlying, ProgressMonitor monitor, Set<Spliterator<T>> openChildren) {
        this.underlying = underlying;
        this.monitor = monitor;
        monitor.setSize(estimateSize());
        this.openChildSpliterators = openChildren;
        this.openChildSpliterators.add(this);
    }

    public ProgressMonitor getProgressBar() {
        return monitor;
    }

    @Override
    public void close() {
        monitor.done();
    }

    private void registerChild(Spliterator<T> child) {
        openChildSpliterators.add(child);
    }

    private void removeThis() {
        openChildSpliterators.remove(this);
        if (openChildSpliterators.size() == 0) close();
        // only closes the progressbar if no spliterator is working anymore
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        boolean r = underlying.tryAdvance(action);
        if (r) {
            monitor.worked(1);
        } else {
            removeThis();
        }
        return r;
    }

    @Override
    public Spliterator<T> trySplit() {
        Spliterator<T> newSplit = underlying.trySplit();
        if (newSplit != null) {
            SpliteratorProgress<T> child = new SpliteratorProgress<>(newSplit, monitor, openChildSpliterators);
            registerChild(child);
            return child;
        } else return null;
    }

    @Override
    public long estimateSize() {
        return underlying.estimateSize();
    }

    @Override
    public int characteristics() {
        return underlying.characteristics();
    }

    @Override // if not overridden, may return null since that is the default Spliterator implementation
    public Comparator<? super T> getComparator() {
        return underlying.getComparator();
    }
}
