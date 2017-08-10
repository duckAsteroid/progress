package org.duck.asteroid.progress.console;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.duck.asteroid.progress.base.AbstractProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.ProgressFormat;

/**
 * A Java logging progress
 */
public class LoggingProgress extends BaseProgressMonitor {
	private final Logger logger;
	private final Level level;
	private final ProgressFormat format;

    public LoggingProgress(final Logger logger, final Level level, final ProgressFormat format) {
        this.logger = logger;
        this.level = level;
        this.format = format;
    }

    @Override
	protected void logUpdate(AbstractProgressMonitor child) {
		logger.log(level, format.format(child));
	}
}
