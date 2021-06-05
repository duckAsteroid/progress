package io.github.duckasteroid.progress.base;

import static org.junit.Assert.assertEquals;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * A helper for doing assertions on a {@link ProgressMonitor}
 */
public class FractionAssert {
  private final long total;
  private long work;
  private ProgressMonitor progress;

  private FractionAssert(long total) {
    this.total = total;
  }

  public static FractionAssert outOf(long total) {
    return new FractionAssert(total);
  }

  public static FractionAssert on(ProgressMonitor progress) {
    return new FractionAssert(progress.getSize()).with(progress);
  }

  public FractionAssert expectedWorkDone(long worked) {
    this.work = worked;
    return this;
  }

  public FractionAssert with(ProgressMonitor progress) {
    this.progress = progress;
    return this;
  }

  /**
   * Given the state of the expected work done - validate the values of the progress
   */
  public FractionAssert check() {
    assertEquals(total, progress.getSize());
    assertEquals(work, progress.getWorkDone());
    double fraction = (double) work / (double) total;
    assertEquals(fraction, progress.getFractionDone(), 0.0001);
    boolean complete = fraction >= 1.0d;
    assertEquals(complete, progress.isDone());
    return this;
  }
}
