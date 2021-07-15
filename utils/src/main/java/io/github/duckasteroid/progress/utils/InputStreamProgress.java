package io.github.duckasteroid.progress.utils;

import io.github.duckasteroid.progress.ProgressMonitor;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an input stream to report 1 unit of work for every byte {@link #read()}.
 * Clients should set the {@link ProgressMonitor#setSize(long)} prior to invoking for this
 * to report correctly.
 * When the stream is {@link #close() closed} the monitor is marked done
 */
public class InputStreamProgress extends InputStream {
  private final transient  InputStream delegate;
  private final transient ProgressMonitor monitor;

  public InputStreamProgress(InputStream delegate, ProgressMonitor monitor) {
    this.delegate = delegate;
    this.monitor = monitor;
  }

  @Override
  public int read() throws IOException {
    if (monitor.isCancelled()) {
      throw new IOException("Read operation cancelled by monitor");
    }
    monitor.worked(1);
    return delegate.read();
  }

  @Override
  public void close() throws IOException {
    monitor.done();
    super.close();
  }
}
