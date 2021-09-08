package io.github.duckasteroid.progress.utils;

import io.github.duckasteroid.progress.ProgressMonitor;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility to wrap a stream in progress monitor.
 * @see SpliteratorProgress for details
 */
public class StreamProgress {
  /**
   * Wrap a stream with a progress monitor. Each item taken from the stream adds one unit of work
   * to the progress.
   * @param stream The stream to wrap
   * @param monitor The monitor to report progress to
   * @param <T> The type of the stream
   * @return A stream that will report progress when read from.
   */
  public static <T> Stream<T> wrapWithProgress(Stream<T> stream, ProgressMonitor monitor) {
    SpliteratorProgress<T> spliterator = //NOPMD
        new SpliteratorProgress<>(stream.spliterator(), monitor);
    boolean parallel =
        (spliterator.characteristics() & Spliterator.CONCURRENT) == Spliterator.CONCURRENT;
    return StreamSupport.stream(spliterator, parallel);
  }
}
