package org.duck.asteroid.progress.slf4j;

import org.duck.asteroid.progress.base.AbstractProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.ProgressFormat;
import org.slf4j.Logger;

/**
 *
 */
public class Slf4JProgress extends BaseProgressMonitor {
    private final Logger logger;
    private final ProgressFormat format;
    private final Level level;

    public Slf4JProgress(final Logger logger, final ProgressFormat format, final Level level) {
        this.logger = logger;
        this.format = format;
        this.level = level;
    }

    interface LogSwitch {
        void write(Logger logger, String message);
    }

    public enum Level implements LogSwitch {
        ERROR {
            @Override
            public void write(final Logger logger, final String message) {
                logger.error(message);
            }
        },
        WARN {
            @Override
            public void write(final Logger logger, final String message) {
                if (logger.isWarnEnabled()) {
                    logger.warn(message);
                }
            }
        },
        INFO {
            @Override
            public void write(final Logger logger, final String message) {
                if (logger.isDebugEnabled()) {
                    logger.info(message);
                }
            }
        },
        DEBUG {
            @Override
            public void write(final Logger logger, final String message) {
                if (logger.isDebugEnabled()) {
                    logger.debug(message);
                }
            }
        },
        TRACE {
            @Override
            public void write(final Logger logger, final String message) {
                if (logger.isTraceEnabled()) {
                    logger.trace(message);
                }
            }
        };
    }

    @Override
    protected void logUpdate(final AbstractProgressMonitor child) {
        String format = this.format.format(child);
        level.write(logger, format);
    }
}
