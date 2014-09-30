package org.duck.asteroid.progress.console;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.duck.asteroid.progress.base.AbstractProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.ProgressFormat;

public class LoggingProgress extends BaseProgressMonitor {
	private Logger logger;
	private Level level;
	private ProgressFormat format;
	
	@Override
	protected void logUpdate(AbstractProgressMonitor child) {
		logger.log(level, format.format(child));
	}
}
