package io.github.duckasteroid.progress.base.format.parse;

import io.github.duckasteroid.progress.base.format.elements.FormatElement;

import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interface to an object that can supply parsers for known tags.
 * This is a {@link ServiceLoader} service interface
 */
public interface FormatParser {
    /**
     * A map of a format parser functions by "tag"
     * @return a map of parser functions
     */
    default Map<String, Function<String, FormatElement>> parsers() {
        return Collections.emptyMap();
    };

    /**
     * Load the registered parsers using {@link ServiceLoader}
     */
    static Map<String, Function<String, FormatElement>> loadParsers() {
        ServiceLoader<FormatParser> serviceLoader = ServiceLoader.load(FormatParser.class);
        return serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(fp -> fp.parsers().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
