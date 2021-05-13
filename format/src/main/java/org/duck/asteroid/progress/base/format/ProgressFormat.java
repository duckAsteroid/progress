package org.duck.asteroid.progress.base.format;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * Interface to objects that can format a progress monitor as a string
 */
public interface ProgressFormat {
    /**
     * Given the monitor produce a string form
     * @param monitor the monitor to format
     * @return the string format
     */
    String format(ProgressMonitor monitor);
}
