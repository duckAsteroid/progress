package io.github.duckasteroid.progress.slf4j.util;

import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple LRU cache based on a {@link LinkedHashMap}.
 */
public class LruCache extends LinkedHashMap<String, ProgressMonitorListener> {
  private static final long serialVersionUID = 71308712074L;

  private final transient int cacheSize;

  public LruCache(int size) {
    super(size, 0.75f, true);
    this.cacheSize = size;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<String, ProgressMonitorListener> eldest) {
    return size() >= cacheSize;
  }
}
