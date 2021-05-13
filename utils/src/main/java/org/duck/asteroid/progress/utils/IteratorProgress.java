package org.duck.asteroid.progress.utils;

import org.duck.asteroid.progress.ProgressMonitor;

import java.util.Collection;
import java.util.Iterator;

public class IteratorProgress<E> implements Iterator<E> {
    private final Iterator<E> delegate;
    private final ProgressMonitor monitor;

    public static <E> Iterator<E> iterator(Collection<E> collection, ProgressMonitor monitor) {
        monitor.setSize(collection.size());
        return new IteratorProgress<>(collection.iterator(), monitor);
    }

    public IteratorProgress(Iterator<E> delegate, ProgressMonitor monitor) {
        this.delegate = delegate;
        this.monitor = monitor;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext() && !monitor.isCancelled();
    }

    @Override
    public E next() {
        monitor.worked(1);
        return delegate.next();
    }

    @Override
    public void remove() {
        delegate.remove();
    }
}
