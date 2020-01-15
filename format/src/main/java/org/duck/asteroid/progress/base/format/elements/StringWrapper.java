package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

public class StringWrapper implements FormatElement {
    private final String before;
    private final String after;
    private final FormatElement wrapped;

    public StringWrapper(String before, FormatElement wrapped, String after) {
        this.before = before;
        this.wrapped = wrapped;
        this.after = after;
    }

    public static StringWrapper prefix(String prefix, FormatElement wrapped) {
        return new StringWrapper(prefix, wrapped, "");
    }

    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        if (wrapped.hasContent(monitor)) {
            sb.append(before);
            wrapped.appendTo(sb, monitor);
            sb.append(after);
        }
    }

    @Override
    public boolean hasContent(ProgressMonitor monitor) {
        return wrapped.hasContent(monitor);
    }
}
