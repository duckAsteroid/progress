package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * Adds the monitor unit to the output
 */
public class Unit implements FormatElement {
    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        if (hasContent(monitor)) {
            sb.append(Utils.sanitize(monitor.getUnit()));
        }
    }

    @Override
    public boolean hasContent(ProgressMonitor monitor) {
        if (monitor == null) return false;

        String unit = monitor.getUnit();
        return Utils.notEmpty(unit);
    }
}
