package io.github.duckasteroid.progress.slf4j;

import static io.github.duckasteroid.progress.slf4j.StandardProgressSequence.EXPECTED_NUM_EVENTS;
import static org.junit.Assert.*;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.ProgressMonitorFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Slf4JProgressTest {

  @Test
  public void testBasicLogging() {
    TestLogger.events.clear();

    // do standard sequence...
    {
      ProgressMonitor progressMonitor =
        ProgressMonitorFactory.newMonitor(Slf4JProgressTest.class.getName(), 10);
      StandardProgressSequence sequence = new StandardProgressSequence(progressMonitor);
      sequence.run();
    }
    assertEquals(EXPECTED_NUM_EVENTS, TestLogger.events.size());
    // standard sequence to another logger (one we ignore)
    {
      ProgressMonitor progressMonitor =
        ProgressMonitorFactory.newMonitor("#ignore", 10);
      StandardProgressSequence sequence = new StandardProgressSequence(progressMonitor);
      sequence.run();
    }
    assertEquals(EXPECTED_NUM_EVENTS, TestLogger.events.size());

    // clear out and log to a root logger we name
    TestLogger.events.clear();
    assertEquals(0, TestLogger.events.size());
    {
      ProgressMonitor progressMonitor =
        ProgressMonitorFactory.newMonitor("#capture", 10);
      StandardProgressSequence sequence = new StandardProgressSequence(progressMonitor);
      sequence.run();
    }
    assertEquals(EXPECTED_NUM_EVENTS, TestLogger.events.size());

    TestLogger.events.clear();
    assertEquals(0, TestLogger.events.size());

    // log to a made up logger
    {
      Logger logger = LoggerFactory.getLogger(getClass().getName() + ".debug");
      ProgressMonitor progressMonitor =
        Slf4jFactory.newMonitorLoggingTo(logger, Slf4JProgress.Level.DEBUG, "Test direct", 10);
      StandardProgressSequence sequence = new StandardProgressSequence(progressMonitor);
      sequence.run();
    }
    assertEquals(EXPECTED_NUM_EVENTS, TestLogger.events.size());
    TestLogger.events.clear();
    assertEquals(0, TestLogger.events.size());
    {
      Logger logger = LoggerFactory.getLogger(getClass().getName() + ".info");
      ProgressMonitor progressMonitor =
        Slf4jFactory.newMonitorLoggingTo(logger, Slf4JProgress.Level.DEBUG, "Test direct", 10);
      StandardProgressSequence sequence = new StandardProgressSequence(progressMonitor);
      sequence.run();
    }
    assertEquals(0, TestLogger.events.size());
  }
}