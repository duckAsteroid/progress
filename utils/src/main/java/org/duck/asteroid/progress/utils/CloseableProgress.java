package org.duck.asteroid.progress.utils;

import org.duck.asteroid.progress.ProgressMonitor;

public class CloseableProgress implements AutoCloseable {
    private final AutoCloseable delegate;
    private final ProgressMonitor monitor;

    public CloseableProgress(AutoCloseable delegate, ProgressMonitor monitor) {
        this.delegate = delegate;
        this.monitor = monitor;
    }

    @Override
    public void close() throws Exception {
        monitor.done();
        delegate.close();
    }
}
