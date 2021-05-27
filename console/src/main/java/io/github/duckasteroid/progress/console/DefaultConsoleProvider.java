package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.base.format.SimpleProgressFormat;
import io.github.duckasteroid.progress.Configuration;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.format.CompoundFormat;
import io.github.duckasteroid.progress.base.format.ProgressFormat;

import java.io.PrintStream;

public class DefaultConsoleProvider {
    private static final String NAMESPACE = "org.duck.asteroid.progress.console.";
    /**
     * @see java.util.ServiceLoader
     */
    public static ProgressMonitorListener provide() {
        // FIXME Read configuration for output, and format
        Configuration cfg = Configuration.getInstance();
        PrintStream output = cfg.getValue(NAMESPACE + "output", s -> "err".equals(s) ? System.err : System.out, System.out);
        ProgressFormat format = cfg.getValue(NAMESPACE + "format", CompoundFormat::parse, SimpleProgressFormat.DEFAULT);
        Boolean multiline = cfg.getBoolean(NAMESPACE + "multiline", false);
        return new ConsoleProgress(output, format, multiline);
    }
}
