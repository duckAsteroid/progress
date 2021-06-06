package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.Configuration;
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
 * @see java.util.ServiceLoader
 */
public class DefaultConsoleProvider extends ConsoleProgress {
  private static final String NAMESPACE = "org.duck.asteroid.progress.console.";

  /**
   * Create an instance of listener for console using config.
   */
  public DefaultConsoleProvider() {
    super(output(Configuration.getInstance()),
        format(Configuration.getInstance()),
        multiline(Configuration.getInstance()));
  }

  private static PrintStream output(Configuration cfg) {
    return cfg
      .getValue(NAMESPACE + "output", s -> "err".equals(s) ? System.err : System.out,
        System.out);
  }

  private static ProgressFormat format(Configuration cfg) {
    return cfg.getValue(NAMESPACE + "format", CompoundFormat::parse,
      SimpleProgressFormat.DEFAULT);
  }

  private static boolean multiline(Configuration cfg) {
    return cfg.getBoolean(NAMESPACE + "multiline", false);
  }
}
