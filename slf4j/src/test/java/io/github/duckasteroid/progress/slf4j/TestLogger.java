package io.github.duckasteroid.progress.slf4j;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestLogger extends AppenderBase<ILoggingEvent> {

  public final static List<ILoggingEvent> events = Collections.synchronizedList(new LinkedList<>());

  @Override
  protected void append(ILoggingEvent eventObject) {
    events.add(eventObject);
  }
}
