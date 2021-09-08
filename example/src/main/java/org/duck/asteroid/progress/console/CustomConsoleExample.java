package org.duck.asteroid.progress.console;

import static io.github.duckasteroid.progress.base.format.elements.ProgressBar.BAR_EQUALS;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.ProgressMonitorFactory;
import io.github.duckasteroid.progress.base.format.CompoundFormat;
import io.github.duckasteroid.progress.base.format.ProgressFormat;
import io.github.duckasteroid.progress.base.format.elements.FormatElement;
import io.github.duckasteroid.progress.base.format.elements.Fraction;
import io.github.duckasteroid.progress.base.format.elements.Percentage;
import io.github.duckasteroid.progress.base.format.elements.ProgressBar;
import io.github.duckasteroid.progress.base.format.elements.Spinner;
import io.github.duckasteroid.progress.base.format.elements.StaticString;
import io.github.duckasteroid.progress.base.format.elements.Status;
import io.github.duckasteroid.progress.base.format.elements.StringWrapper;
import io.github.duckasteroid.progress.base.format.elements.TaskName;
import io.github.duckasteroid.progress.console.Colourizer;
import io.github.duckasteroid.progress.console.ConsoleProgress;

public class CustomConsoleExample {
  /**
   * This is an example of a client application that wishes to provide a progress monitor to other
   * code.
   * This example is not concerned with the details of how that monitor is configured so obtains an
   * instance from {@link ProgressMonitorFactory}
   *
   * @param args Command line arguments (none required)
   */
  public static void main(String[] args) {
    ProgressFormat format = new CompoundFormat(new FormatElement[] {
        new Colourizer(Colourizer.Color.GREEN.getCode(),
          StringWrapper.wrap("[", new ProgressBar(10, new char[]{'C', '<', 'o'}), "]")),
        StaticString.WHITESPACE,
        new Colourizer(Colourizer.Color.YELLOW.getCode(),
          new Spinner(Spinner.SPINNER_SLASHES)),
        StaticString.WHITESPACE,
        new Fraction(),
        StaticString.WHITESPACE,
        new TaskName(),
        StaticString.WHITESPACE,
        StringWrapper.prefix("- ", new Status())
    });
    // obtain a monitor instance
    ProgressMonitor monitor = ConsoleProgress.createConsoleMonitor(format, false);

    monitor.setSize(2);

    WorkerExample worker1 = new WorkerExample(500, 20);
    worker1.doSomething(monitor.newSubTask("First worker"));

    WorkerExample worker2 = new WorkerExample(250, 15);
    worker2.doSomething(monitor.newSubTask("Second worker"));

    monitor.done();
  }
}
