package org.duck.asteroid.progress.base.format;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * Interface to objects that can format a progress monitor as a string
 */
public interface ProgressFormat {
    String format(ProgressMonitor monitor);
}
