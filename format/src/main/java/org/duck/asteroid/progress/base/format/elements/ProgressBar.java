package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

import java.util.Arrays;

/**
 * Adds a textual progress bar to the format output
 */
public class ProgressBar implements FormatElement {
    /** A bar pattern like this: <code>[====>   ]</code> */
    public static final char[] BAR_EQUALS = new char[] {'[', '=', '>', ' ', ']'};

    /** show a "fake" character progress bar using the first 4 chars from this array */
    private final char[] barChars;
    private final String fullBar;
    private final String emptyBar;

    /** how wide (in characters) to make the formatted message */
    private final int width;

    public ProgressBar(int width, char[] barChars ) {
        if (width <= 1)
            throw new IllegalArgumentException("Width must be > 1");
        this.width = width;
        this.barChars = barChars;
        if (barChars != null) {
            if (barChars.length >= 4) {
                // create full length strings for the bar to avoid doing on every progress update
                char[] tmp = new char[width];
                Arrays.fill(tmp, barChars[1]);
                fullBar = new String(tmp);
                Arrays.fill(tmp, barChars[3]);
                emptyBar = new String(tmp);
            }
            else {
                throw new IllegalArgumentException("Bar chars should contain 4 chars");
            }
        }
        else {
            throw new IllegalArgumentException("Bar chars should not be null");
        }
    }

    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        int percent = Math.max(0, (int) (monitor.getWorkDone() * width / monitor.getSize()));;
        sb.append(barChars[0])
            .append(fullBar.substring(width - percent))
            .append(barChars[2])
            .append(emptyBar.substring(percent))
            .append(barChars[4]);
    }
}
