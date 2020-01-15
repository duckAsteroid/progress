package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

public interface FormatElement {
    void appendTo(StringBuilder sb, ProgressMonitor monitor);
    default boolean hasContent(ProgressMonitor monitor) {
        return true;
    }
}
