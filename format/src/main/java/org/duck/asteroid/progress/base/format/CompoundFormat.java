package org.duck.asteroid.progress.base.format;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.elements.*;

import java.util.*;

import static org.duck.asteroid.progress.base.format.elements.ProgressBar.BAR_EQUALS;
import static org.duck.asteroid.progress.base.format.elements.Spinner.SPINNER_SLASHES;

/**
 * Creates a string suitable for "advanced" console output (i.e. not typically supported by IDEs)
 * The string is overwritten by itself when printed without carriage returns (e.g. <code>System.out.print()</code>)
 */
public class CompoundFormat implements ProgressFormat {
    private static boolean valid(char[] c, int minLength) {
        return c != null && c.length >= minLength;
    }

    private final List<FormatElement> elements = new ArrayList<>(7);

    public static CompoundFormat MAXIMAL = new CompoundFormat( new FormatElement[]{
                new TaskName(), StaticString.CONDITIONAL_WHITESPACE,
                new Spinner(SPINNER_SLASHES), StaticString.WHITESPACE,
                new Percentage(), StaticString.WHITESPACE,
                new ProgressBar(50, BAR_EQUALS), StaticString.WHITESPACE,
                new Fraction(), StaticString.WHITESPACE,
                StringWrapper.prefix("- ", new Status())
        });

    public CompoundFormat(FormatElement ... elements) {
        this(Arrays.asList(elements));
    }

    public CompoundFormat(Collection<FormatElement> elements) {
        this.elements.addAll(elements);
    }

    @Override
    public String format(ProgressMonitor source) {
        StringBuilder string = new StringBuilder();
        for(FormatElement fe : elements) {
            fe.appendTo(string, source);
        }
        return string.toString();
    }

}
