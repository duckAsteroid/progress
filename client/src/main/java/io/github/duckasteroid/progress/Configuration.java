package io.github.duckasteroid.progress;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Configuration class used by clients (and supporting libraries) to load
 * statically configured monitoring.
 */
public class Configuration {
  private static final Configuration singleton = load();
  private final transient Properties source;

  public Configuration(Properties source) {
    this.source = source;
  }

  private static Configuration load() {
    // FIXME Attempt to load config files from classpath a la logging frameworks
    return new Configuration(System.getProperties());
  }

  public static Configuration getInstance() {
    return singleton;
  }

  public Optional<String> getStringValue(String prop) {
    return Optional.ofNullable(source.getProperty(prop));
  }

  public boolean hasValueFor(String name) {
    return getStringValue(name).isPresent();
  }

  public <T> T getValue(String name, Function<String, T> parser, T defaultValue) {
    return getStringValue(name).map(parser::apply).orElse(defaultValue);
  }

  public Integer getInteger(String name, Integer defaultValue) {
    return getValue(name, Integer::parseInt, defaultValue);
  }

  public Long getLong(String name, Long defaultValue) {
    return getValue(name, Long::parseLong, defaultValue);
  }

  public Boolean getBoolean(String name, Boolean defaultValue) {
    return getValue(name, Boolean::parseBoolean, defaultValue);
  }

  public String getString(String name, String defaultValue) {
    return getValue(name, Function.identity(), defaultValue);
  }

}
