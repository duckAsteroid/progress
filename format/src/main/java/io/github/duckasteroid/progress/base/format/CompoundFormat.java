package io.github.duckasteroid.progress.base.format;

import static io.github.duckasteroid.progress.base.format.elements.ProgressBar.BAR_EQUALS;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.format.elements.FormatElement;
import io.github.duckasteroid.progress.base.format.elements.Fraction;
import io.github.duckasteroid.progress.base.format.elements.Percentage;
import io.github.duckasteroid.progress.base.format.elements.ProgressBar;
import io.github.duckasteroid.progress.base.format.elements.Spinner;
import io.github.duckasteroid.progress.base.format.elements.StaticString;
import io.github.duckasteroid.progress.base.format.elements.Status;
import io.github.duckasteroid.progress.base.format.elements.StringWrapper;
import io.github.duckasteroid.progress.base.format.elements.TaskName;
import io.github.duckasteroid.progress.base.format.parse.FormatParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * A compound format made up by delegating to a list of {@link FormatElement}s.
 */
public class CompoundFormat implements ProgressFormat {

  public static CompoundFormat MAXIMAL = new CompoundFormat(new FormatElement[] {
      new TaskName(), StaticString.CONDITIONAL_WHITESPACE,
      new Spinner(Spinner.SPINNER_SLASHES), StaticString.WHITESPACE,
      new Percentage(), StaticString.WHITESPACE,
      StringWrapper.wrap("[", new ProgressBar(50, BAR_EQUALS), "]"), StaticString.WHITESPACE,
      new Fraction(), StaticString.WHITESPACE,
      StringWrapper.prefix("- ", new Status())
  });
  private final List<FormatElement> elements = new ArrayList<>(7); //NOPMD - method clash

  public CompoundFormat(FormatElement... elements) {
    this(Arrays.asList(elements));
  }

  public CompoundFormat(Collection<FormatElement> elements) {
    this.elements.addAll(elements);
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  static List<FormatElement> parse(String config,
                                   Map<String, Function<String, FormatElement>> helpers) {
    LinkedList<FormatElement> result = new LinkedList<>();
    StringTokenizer segments = new StringTokenizer(config, "%", true);

    ParseState state = ParseState.TEXT;
    int pos = 0;
    while (segments.hasMoreTokens()) {
      String s = segments.nextToken();
      pos += s.length();
      if ("%".equals(s)) {
        if (state == ParseState.TEXT) {
          state = ParseState.ENTITY;
        } else {
          state = ParseState.TEXT;
        }
        continue; // skip...
      }

      switch (state) {
        default:
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

  public static CompoundFormat parse(String s) {
    return new CompoundFormat(parse(s, FormatParser.loadParsers()));
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

  @Override
  public String format(ProgressMonitor source) {
    StringBuilder string = new StringBuilder();
    for (FormatElement fe : elements) {
      fe.appendTo(string, source);
    }
    return string.toString();
  }

  public List<FormatElement> elements() {
    return Collections.unmodifiableList(elements);
  }

  private enum ParseState {
    TEXT, ENTITY
  }

}
