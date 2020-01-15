package org.duck.asteroid.progress.slf4j;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorEvent;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;
import org.duck.asteroid.progress.base.event.ProgressUpdateType;
import org.duck.asteroid.progress.base.format.ProgressFormat;
import org.slf4j.Logger;

/**
 *
 */
public class Slf4JProgress implements ProgressMonitorListener {
    private final Logger logger;
    private final ProgressFormat format;
    private final Level level;

    public Slf4JProgress(final Logger logger, final ProgressFormat format, final Level level) {
        this.logger = logger;
        this.format = format;
        this.level = level;
    }

    interface LogSwitch {
        /**
         * Is this level of logging enabled on the logger
         * @param logger the logger
         * @return true if this level of logging is enabled
         */
        boolean isEnabled(Logger logger);

        /**
         * Write a log message at this level
         * @param logger
         * @param message
         */
        void write(Logger logger, String message);
    }

    public enum Level implements LogSwitch {
        ERROR {
            @Override
            public boolean isEnabled(Logger logger) {
                return logger.isErrorEnabled();
            }

            @Override
            public void write(final Logger logger, final String message) {
                logger.error(message);
            }
        },
        WARN {
            @Override
            public boolean isEnabled(Logger logger) {
                return logger.isWarnEnabled();
            }

            @Override
            public void write(final Logger logger, final String message) {
                logger.warn(message);
            }
        },
        INFO {
            @Override
            public boolean isEnabled(Logger logger) {
                return logger.isInfoEnabled();
            }
            @Override
            public void write(final Logger logger, final String message) {
                logger.info(message);
            }
        },
        DEBUG {
            @Override
            public boolean isEnabled(Logger logger) {
                return logger.isDebugEnabled();
            }
            @Override
            public void write(final Logger logger, final String message) {
                logger.debug(message);
            }
        },
        TRACE {
            @Override
            public boolean isEnabled(Logger logger) {
                return logger.isTraceEnabled();
            }
            @Override
            public void write(final Logger logger, final String message) {
                logger.trace(message);
            }
        };
    }

    @Override
    public void logUpdate(final ProgressMonitorEvent evt) {
        if (level.isEnabled(logger)) {
            String format = this.format.format(evt.getSource());
            level.write(logger, format);
        }
    }
}
