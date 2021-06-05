package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.Configuration;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.format.CompoundFormat;
import io.github.duckasteroid.progress.base.format.ProgressFormat;
import io.github.duckasteroid.progress.base.format.SimpleProgressFormat;
import java.io.PrintStream;

/**
 * A service loader provider of a progress monitor listener that uses console.
 * The instance can be configured via the parameters:
 * <ul>
 *     <li><pre>org.duck.asteroid.progress.console.output</pre>Use <pre>err</pre> for System err,
 *     otherwise System out (default)</li>
 *     <li><pre>org.duck.asteroid.progress.console.format</pre>Format string for
 *     {@link CompoundFormat#parse(String)}. Default is {@link SimpleProgressFormat#DEFAULT}</li>
 *     <li><pre>org.duck.asteroid.progress.console.multiline</pre>Is the output single line or
 *     multi line (boolean). Default is false</li>
 * </ul>
 */
public class DefaultConsoleProvider {
  private static final String NAMESPACE = "org.duck.asteroid.progress.console.";

  /**
   * Create an instance of listener for console using config.
   * @return Instance of progress monitor listener that directs to console.
   * @see java.util.ServiceLoader
   */
  public static ProgressMonitorListener provide() {
    // FIXME Read configuration for output, and format
    Configuration cfg = Configuration.getInstance();
    PrintStream output = cfg
        .getValue(NAMESPACE + "output", s -> "err".equals(s) ? System.err : System.out,
        System.out);
    ProgressFormat format =
        cfg.getValue(NAMESPACE + "format", CompoundFormat::parse,
        SimpleProgressFormat.DEFAULT);
    Boolean multiline = cfg.getBoolean(NAMESPACE + "multiline", false);
    return new ConsoleProgress(output, format, multiline);
  }
}
