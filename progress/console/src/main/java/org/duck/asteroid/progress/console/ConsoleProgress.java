package org.duck.asteroid.progress.console;

import java.io.PrintStream;

import org.duck.asteroid.progress.base.AbstractProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.ProgressFormat;

/**
 * A simple progress monitor that writes all progress to the console
 */
public class ConsoleProgress extends BaseProgressMonitor {
	private PrintStream output;
	private ProgressFormat formatter;
	
	public ConsoleProgress(PrintStream output, ProgressFormat formatter) {
		this.output = output;
		this.formatter = formatter;
	}
	
	public ConsoleProgress() {
		this(System.out, ProgressFormat.DEFAULT);
	}

	@Override
	protected void logUpdate(AbstractProgressMonitor child) {
		output.println(formatter.format(child));
	}
}
