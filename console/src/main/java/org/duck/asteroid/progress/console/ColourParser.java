package org.duck.asteroid.progress.console;

import org.duck.asteroid.progress.base.format.elements.FormatElement;
import org.duck.asteroid.progress.base.format.parse.FormatParser;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ColourParser implements FormatParser {
    @Override
    public Map<String, Function<String, FormatElement>> parsers() {
        return Collections.singletonMap("color", s -> parse(s));
    }

    public static Colourizer parse(String s) {
        Optional<Colourizer.Color> optionalColor = Colourizer.Color.parse(s);
        return new Colourizer(optionalColor.map(Colourizer.Color::getCode).orElse(s));
    }
}
