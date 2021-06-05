package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.format.ProgressFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java logging progress.
 */
public class LoggingProgress implements ProgressMonitorListener {
  private final transient Logger logger; //NOPMD - we are not using our logger here...
  private final transient Level level;
  private final transient ProgressFormat format;

  /**
   * Create new java logging progress wrapper.
   * @param logger the Java logger to write to
   * @param level the level to write at
   * @param format the format to use for messages
   */
  public LoggingProgress(final Logger logger, final Level level, final ProgressFormat format) {
    this.logger = logger;
    this.level = level;
    this.format = format;
  }

  @Override
  public void logUpdate(ProgressMonitorEvent event) {
    if (logger.isLoggable(level)) {
      logger.log(level, format.format(event.getSource()));
    }
  }
}
