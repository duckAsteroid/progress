package org.duck.asteroid.progress.console;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.format.ProgressFormat;

import java.io.PrintStream;

/**
 * A simple progress monitor that writes all progress to the console
 */
public class ConsoleProgress extends BaseProgressMonitor {
	private final PrintStream output;
	private final ProgressFormat formatter;

	public ConsoleProgress(PrintStream output, ProgressFormat formatter) {
		this.output = output;
		this.formatter = formatter;
	}
	
	public ConsoleProgress() {
		this(System.out, ProgressFormat.DEFAULT);
	}

	@Override
	public void logUpdate(ProgressMonitor source) {
		output.println(formatter.format(source));
	}
}
