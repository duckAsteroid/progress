package org.duck.asteroid.progress.console;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.AbstractProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorEvent;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;
import org.duck.asteroid.progress.base.event.ProgressUpdateType;
import org.duck.asteroid.progress.base.format.ProgressFormat;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Semaphore;

/**
 * A simple progress monitor that writes all progress to the console
 */
public class ConsoleProgress implements ProgressMonitorListener {
	public static final String CLEAR_LINE = "\033[2K";
	public static final String REWIND = "\u001B[1F";

	private final PrintStream output;
	private final ProgressFormat formatter;

	private Semaphore semaphore = new Semaphore(1, false);

	public ConsoleProgress(PrintStream output, ProgressFormat formatter) {
		this.output = output;
		this.formatter = formatter;
	}

	@Override
	public void logUpdate(final ProgressMonitorEvent event) {
		// permits only one thread to update the console
		if (semaphore.tryAcquire()) {
			try {
				outputToConsole(event.getSource(), event.getType() == ProgressUpdateType.DONE);
			} finally {
				semaphore.release();
			}
		}
	}

	private void outputToConsole(ProgressMonitor source, boolean done) {
		// print the context (if any)
		List<ProgressMonitor> context = source.getContext();
		for (int i = 0; i < context.size(); i++) {
			output.print('\r');
			output.print(CLEAR_LINE);
			output.print(formatter.format(context.get(i)));
			output.print('\n'); // get to the line to update
		}
		// do the update to the line for the updated progress
		boolean root = context.isEmpty();

		output.print('\r');
		output.print(CLEAR_LINE);

		// only write if root OR not done
		if (root || !done ) {
			String format = formatter.format(source);
			output.print(format);
		}

		// rewind (if necessary)
		for(int i = 0; i < context.size(); i++) {
			output.print('\r');
			output.print(REWIND);
		}

		output.flush();
	}

	public static ProgressMonitor createConsoleMonitor(ProgressFormat format, boolean multiline) {
		BaseProgressMonitor monitor = new BaseProgressMonitor();
		monitor.addProgressMonitorListener(new ConsoleProgress(System.out, format));
		return monitor;
	}
}
