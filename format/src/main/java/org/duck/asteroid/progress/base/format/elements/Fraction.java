package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * Adds the fraction workDone/size to the format output
 */
public class Fraction implements FormatElement {
    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        final int chars = (int) Math.floor(Math.log10(monitor.getSize())) + 1;
        sb.append(String.format("%"+chars+"d/%"+chars+"d", monitor.getWorkDone(), monitor.getSize()));
    }
}
