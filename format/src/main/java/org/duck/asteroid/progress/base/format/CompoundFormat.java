package org.duck.asteroid.progress.base.format;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.elements.*;

import java.util.*;

import static org.duck.asteroid.progress.base.format.elements.ProgressBar.BAR_EQUALS;
import static org.duck.asteroid.progress.base.format.elements.Spinner.SPINNER_SLASHES;

/**
 * A compound format made up by delegating to a list of {@link FormatElement}s
 */
public class CompoundFormat implements ProgressFormat {

    private final List<FormatElement> elements = new ArrayList<>(7);

    public static CompoundFormat MAXIMAL = new CompoundFormat( new FormatElement[]{
                new TaskName(), StaticString.CONDITIONAL_WHITESPACE,
                new Spinner(SPINNER_SLASHES), StaticString.WHITESPACE,
                new Percentage(), StaticString.WHITESPACE,
                StringWrapper.wrap("[", Colourizer.wrap(new ProgressBar(50, BAR_EQUALS), Colourizer.Color.GREEN), "]"), StaticString.WHITESPACE,
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
