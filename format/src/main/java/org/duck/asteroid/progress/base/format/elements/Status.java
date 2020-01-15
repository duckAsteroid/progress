package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * Adds the monitor status message to the output
 */
public class Status implements FormatElement {
    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        if (hasContent(monitor)) {
            sb.append(Utils.sanitize(monitor.getStatus()));
        }
    }

    @Override
    public boolean hasContent(ProgressMonitor monitor) {
        if (monitor == null) return false;

        String status = monitor.getStatus();
        return (status != null && status.length() > 0);
    }
}
