package io.github.duckasteroid.progress.slf4j.util;

import io.github.duckasteroid.progress.base.event.ProgressUpdateType;
import io.github.duckasteroid.progress.slf4j.Slf4JProgress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A map that returns the same value - no matter what key.
 */
public class SingleLevelMap implements Map<ProgressUpdateType, Slf4JProgress.Level> {
  private final transient Slf4JProgress.Level value;

  public SingleLevelMap(Slf4JProgress.Level value) {
    this.value = value;
  }

  @Override
  public int size() {
    return ProgressUpdateType.values().length;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean containsKey(Object key) {
    return key != null && key instanceof ProgressUpdateType;
  }

  @Override
  public boolean containsValue(Object value) {
    return this.value.equals(value);
  }

  @Override
  public Slf4JProgress.Level get(Object key) {
    return value;
  }

  @Override
  public Slf4JProgress.Level put(ProgressUpdateType key, Slf4JProgress.Level value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Slf4JProgress.Level remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends ProgressUpdateType, ? extends Slf4JProgress.Level> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<ProgressUpdateType> keySet() {
    return Set.of(ProgressUpdateType.values());
  }

  @Override
  public Collection<Slf4JProgress.Level> values() {
    return Collections.singleton(value);
  }

  @Override
  public Set<Entry<ProgressUpdateType, Slf4JProgress.Level>> entrySet() {
    return keySet().stream().map(k -> new Entry<ProgressUpdateType, Slf4JProgress.Level>() {
      @Override
      public ProgressUpdateType getKey() {
        return k;
      }

      @Override
      public Slf4JProgress.Level getValue() {
        return value;
      }

      @Override
      public Slf4JProgress.Level setValue(Slf4JProgress.Level value) {
        throw new UnsupportedOperationException();
      }
    }).collect(Collectors.toSet());
  }
}
