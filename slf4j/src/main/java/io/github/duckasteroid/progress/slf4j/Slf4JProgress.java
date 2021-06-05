package io.github.duckasteroid.progress.slf4j;

import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.event.ProgressUpdateType;
import io.github.duckasteroid.progress.base.format.ProgressFormat;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * A progress monitor listener that reports progress via an SLF4J Logger instance
 */
public class Slf4JProgress implements ProgressMonitorListener {
    /** The logger to report to */
    private transient final Logger logger; // NOPMD - this is not OUR logger
    /** The format of the logged progress */
    private transient final ProgressFormat format;
    /** A level to report to the logger for each event */
    private transient final Map<ProgressUpdateType, Level> levels;

    public Slf4JProgress(final Logger logger, final ProgressFormat format, final Map<ProgressUpdateType, Level> level) {
        this.logger = logger;
        this.format = format;
        this.levels = new EnumMap<>(level);
    }

    /**
     * Implemented by the log level switch to do enabled and logging
     */
    interface LogSwitch {
        /**
         * Is this level of logging enabled on the logger
         * @param logger the logger
         * @return true if this level of logging is enabled
         */
        boolean isEnabled(Logger logger);

        /**
         * Write a log message at this level
         * @param logger the logger to write to
         * @param message the message to log
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
        Optional<Level> optLevel = Optional.ofNullable(levels.get(evt.getType()));
        if (optLevel.isPresent()) {
            Level level = optLevel.get();
            if (level.isEnabled(logger)) {
                String format = this.format.format(evt.getSource());
                level.write(logger, format);
            }
        }
    }
}
