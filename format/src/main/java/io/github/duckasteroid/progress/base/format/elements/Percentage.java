package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Adds the percentage XX% to the format output
 */
public class Percentage implements FormatElement {
    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        int percent = Math.min((int)(100 * monitor.getFractionDone()), 100);
        sb.append(String.format("%3d%%", percent));
    }
}
