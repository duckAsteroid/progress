package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * A piece of the formatting output
 */
public interface FormatElement {
    /**
     * Write the output of this format element to the string for the given
     * monitor state
     * @param sb the place to write output
     * @param monitor the monitor to capture state from
     */
    void appendTo(StringBuilder sb, ProgressMonitor monitor);

    /**
     * Does this element produce any output for the given monitor
     * @param monitor the monitor to test for output
     * @return true if calling {@link #appendTo(StringBuilder, ProgressMonitor)} would
     * result in output
     */
    default boolean hasContent(ProgressMonitor monitor) {
        return true;
    }

    /**
     * A kind of element that wraps another element
     */
    interface Wrapping extends FormatElement {
        void setWrapped(FormatElement element);
        boolean isWrapping();
    }
}
