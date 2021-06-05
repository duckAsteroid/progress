package io.github.duckasteroid.progress.utils;

import io.github.duckasteroid.progress.ProgressMonitor;

public class CloseableProgress implements AutoCloseable {
    private transient final AutoCloseable delegate;
    private transient final ProgressMonitor monitor;

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
