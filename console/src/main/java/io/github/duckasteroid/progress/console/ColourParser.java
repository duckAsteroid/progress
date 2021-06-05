package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.base.format.elements.FormatElement;
import io.github.duckasteroid.progress.base.format.parse.FormatParser;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ColourParser implements FormatParser {
  public static Colourizer parse(String s) {
    Optional<Colourizer.Color> optionalColor = Colourizer.Color.parse(s);
    return new Colourizer(optionalColor.map(Colourizer.Color::getCode).orElse(s));
  }

  @Override
  public Map<String, Function<String, FormatElement>> parsers() {
    return Collections.singletonMap("color", s -> parse(s));
  }
}
