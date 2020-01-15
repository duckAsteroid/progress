package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * Adds the monitor taskname to the output
 */
public class TaskName implements FormatElement {
    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        sb.append(Utils.sanitize(monitor.getTaskName()));
    }
}
