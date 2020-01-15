package org.duck.asteroid.progress.console;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorEvent;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;
import org.duck.asteroid.progress.base.event.ProgressUpdateType;
import org.duck.asteroid.progress.base.format.ProgressFormat;

/**
 * A Java logging progress
 */
public class LoggingProgress implements ProgressMonitorListener {
	private final Logger logger;
	private final Level level;
	private final ProgressFormat format;

    public LoggingProgress(final Logger logger, final Level level, final ProgressFormat format) {
        this.logger = logger;
        this.level = level;
        this.format = format;
    }

    @Override
    public void logUpdate(ProgressMonitorEvent event)  {
        if (logger.isLoggable(level)) {
            logger.log(level, format.format(event.getSource()));
        }
	}
}
