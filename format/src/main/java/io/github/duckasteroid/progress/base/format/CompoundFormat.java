package io.github.duckasteroid.progress.base.format;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.format.elements.*;
import io.github.duckasteroid.progress.base.format.elements.*;
import io.github.duckasteroid.progress.base.format.parse.FormatParser;

import java.util.*;
import java.util.function.Function;

import static io.github.duckasteroid.progress.base.format.elements.ProgressBar.BAR_EQUALS;

/**
 * A compound format made up by delegating to a list of {@link FormatElement}s
 */
public class CompoundFormat implements ProgressFormat {

    private final List<FormatElement> elements = new ArrayList<>(7);

    public static CompoundFormat MAXIMAL = new CompoundFormat( new FormatElement[]{
                new TaskName(), StaticString.CONDITIONAL_WHITESPACE,
                new Spinner(Spinner.SPINNER_SLASHES), StaticString.WHITESPACE,
                new Percentage(), StaticString.WHITESPACE,
                StringWrapper.wrap("[", new ProgressBar(50, BAR_EQUALS), "]"), StaticString.WHITESPACE,
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

    public List<FormatElement> elements() {
        return Collections.unmodifiableList(elements);
    }

    private enum ParseState { TEXT, ENTITY}

    static List<FormatElement> parse(String config, Map<String, Function<String, FormatElement>> helpers) {
        final Function<String, FormatElement> staticString = (s) -> new StaticString(s);
        LinkedList<FormatElement> result = new LinkedList<>();
        StringTokenizer segments = new StringTokenizer(config, "%", true);

        ParseState state = ParseState.TEXT;
        int pos = 0;
        while(segments.hasMoreTokens()) {
            String s = segments.nextToken();
            pos += s.length();
            if ("%".equals(s)) {
                if (state == ParseState.TEXT) {
                    state = ParseState.ENTITY;
                }
                else {
                    state = ParseState.TEXT;
                }
                continue; // skip...
            }

            switch (state) {
                case TEXT:
                    FormatElement string = new StaticString(s);
                    add(string, result);
                    break;
                case ENTITY:
                    String[] split = s.split(":");
                    Function<String, FormatElement> elementBuilder = helpers.get(split[0]);
                    FormatElement formatElement = elementBuilder.apply(split.length > 1 ? split[1] : "");
                    add(formatElement, result);
                    break;
            }
        }
        return result;
    }

    private static void add(FormatElement formatElement, LinkedList<FormatElement> result) {
        if (formatElement != null) {
            if (result.size() > 0) {
                FormatElement previous = result.getLast();
                if (previous instanceof FormatElement.Wrapping) {
                    // already wrapping?
                    if (!((FormatElement.Wrapping) previous).isWrapping()) {
                        ((FormatElement.Wrapping) previous).setWrapped(formatElement);
                        return;
                    }
                }
            }
            // add the
            result.add(formatElement);
        }
    }

    public static CompoundFormat parse(String s) {
        return new CompoundFormat(parse(s, FormatParser.loadParsers()));
    }

}
