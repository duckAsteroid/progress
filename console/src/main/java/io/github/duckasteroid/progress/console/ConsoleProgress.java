package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.event.ProgressUpdateType;
import io.github.duckasteroid.progress.base.format.ProgressFormat;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A simple progress monitor that writes all progress to the console
 */
// FIXME Only log for max depth X of monitors?
// FIXME Use config to setup the format and the colouring
public class ConsoleProgress implements ProgressMonitorListener {
	public static final String CLEAR_LINE = "\033[2K";
	public static final String CURSOR_UP_1 = "\033[1A";
	public static final String CURSOR_DOWN_1 = "\033[1B";
	public static final String ERASE_DOWN = "\033[J";

	/** The target output stream for this */
	private final PrintStream output;
	/** Formatter to use for events */
	private final ProgressFormat formatter;
	/** Print all active monitors - or just the updated */
	private final boolean multiline;

	/** used to permit one thread at a time to update the console */
	private Semaphore semaphore = new Semaphore(1, false);
	/** A cache of the console commands required to erase the last output */
	private StringBuilder eraser = new StringBuilder();

	public ConsoleProgress(PrintStream output, ProgressFormat formatter, boolean multiline) {
		this.output = output;
		this.formatter = formatter;
		this.multiline = multiline;
	}

	@Override
	public void logUpdate(final ProgressMonitorEvent event) {
		// permits only one thread to update the console
		// (or the formatting goes to hell)
		if (semaphore.tryAcquire()) {
			// only one thread in here at a time - any others concurrently are ignored
			try {
				outputToConsole(event);
			} finally {
				semaphore.release();
			}
		}
	}

	private void outputToConsole(ProgressMonitorEvent event) {
		if (event.getType() != ProgressUpdateType.DONE) {
			List<ProgressMonitor> toPrint;
			// FIXME implement max depth... == 1?
			if (multiline) {
				toPrint = event.getRoot().getAllActive();
			} else {
				toPrint = Collections.singletonList(event.getSource());
			}

			// erase previous output
			if (eraser.length() > 0) {
				output.print(eraser.toString());
				eraser = new StringBuilder(eraser.length());
			}

			// print each monitor on a new line
			StringBuilder rewinder = new StringBuilder();
			for (ProgressMonitor monitor : toPrint) {
				String formatted = formatter.format(monitor);
				output.println(formatted);
				// build up a series of rewind commands to return the cursor
				// for each line we print above
				rewinder.append(CURSOR_UP_1);
				eraser.append(CLEAR_LINE + CURSOR_DOWN_1);
			}
			eraser.append("\r" + rewinder.toString());

			// rewind the current console
			output.print(rewinder.toString());

			output.flush();
		}
	}

	public static ProgressMonitor createConsoleMonitor(ProgressFormat format, boolean multiline) {
		BaseProgressMonitor monitor = new BaseProgressMonitor();
		monitor.addProgressMonitorListener(new ConsoleProgress(System.out, format, multiline));
		return monitor;
	}
}
