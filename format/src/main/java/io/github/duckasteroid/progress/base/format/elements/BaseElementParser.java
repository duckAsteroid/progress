package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.base.format.parse.FormatParser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BaseElementParser implements FormatParser {
    public Map<String, Function<String, FormatElement>> parsers() {
        HashMap<String, Function<String, FormatElement>> result = new HashMap<>();
        result.put("frac", s -> new Fraction());
        result.put("pct", s -> new Percentage());
        result.put("prog", s -> new ProgressBar(Integer.parseInt(s.substring(3)), s.substring(0, 3).toCharArray()));
        result.put("spin", s -> new Spinner(s.toCharArray()));
        result.put("str", s -> new StaticString(s));
        result.put("stat", s -> new Status());
        result.put("name", s -> new TaskName());
        result.put("unit", s -> new Unit());
        return result;
    }
}
