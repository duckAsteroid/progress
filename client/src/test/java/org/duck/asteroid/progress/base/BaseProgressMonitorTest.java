package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorEvent;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;
import org.duck.asteroid.progress.base.event.ProgressUpdateType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class BaseProgressMonitorTest {

	private BaseProgressMonitor subject = null;

	private class RecordedEvent {
		public final ProgressMonitorEvent event;
		public final long timestamp = System.nanoTime();
		public final long workDone;
		public final long size;
		public final boolean done;
		public final boolean cancelled;

		private RecordedEvent(ProgressMonitorEvent event) {
			this.event = event;
			ProgressMonitor source = event.getSource();
			this.workDone = source.getWorkDone();
			this.size = source.getSize();
			this.done = source.isDone();
			this.cancelled = source.isCancelled();
		}

		@Override
		public String toString() {
			return "RecordedEvent{" +
					"type=" + event.getType() +
					", timestamp=" + timestamp +
					", workDone=" + workDone +
					", size=" + size +
					", done=" + done +
					", cancelled=" + cancelled +
					'}';
		}
	}

	// used from multiple threads - this is not the fastest; but safe for our test
	final List<RecordedEvent> recording = Collections.synchronizedList(new ArrayList<>(5));

	final ProgressMonitorListener recorder = new ProgressMonitorListener() {
		@Override
		public void logUpdate(ProgressMonitorEvent event) {
			recording.add(new RecordedEvent(event));
		}
	};

	@Before
	public void setUp() throws Exception {
		subject = new BaseProgressMonitor(100);
		subject.addProgressMonitorListener(recorder);
	}

	@After
	public void tearDown() throws Exception {
		subject = null;
		recording.clear();
	}


	/**
	 * When we update the simple (0..1) progress things behave the way we expect
	 * we check that the correct events are published
	 */
	@Test
	public void simpleUpdateTest() {


		FractionAssert fractionAssert = FractionAssert.on(subject);
		fractionAssert.expectedWorkDone(0).check();
		assertEquals(0, recording.size());

		subject.worked(50L);
		fractionAssert.expectedWorkDone(50).check();
		assertEquals(1, recording.size());
		ProgressMonitorEvent evt = recording.get(0).event;
		assertEquals(subject, evt.getSource());
		assertEquals(ProgressUpdateType.WORK, evt.getType());

		subject.setStatus("Just a test");
		fractionAssert.expectedWorkDone(50).check();
		assertEquals(2, recording.size());
		evt = recording.get(1).event;
		assertEquals(subject, evt.getSource());
		assertEquals(ProgressUpdateType.STATUS, evt.getType());

		subject.worked(50L);
		fractionAssert.expectedWorkDone(100).check();
		assertEquals(4, recording.size());
		evt = recording.get(2).event;
		assertEquals(subject, evt.getSource());
		assertEquals(ProgressUpdateType.WORK, evt.getType());
		evt = recording.get(3).event;
		assertEquals(subject, evt.getSource());
		assertEquals(ProgressUpdateType.DONE, evt.getType());

		subject.worked(50L);
		fractionAssert.expectedWorkDone(150).check();
		// no more events - as was "done" in last call to #worked
		assertEquals(4, recording.size());

		subject.done();
		fractionAssert.expectedWorkDone(150).check();
		// no more events - as was "done" in last call to #worked
		assertEquals(4, recording.size());
	}

	@Test
	public void testSimpleSubTasks() throws IOException {
		final int STEP_SIZE = 10;
		for(int i= 0; i < subject.getSize(); i += STEP_SIZE) {
			ProgressMonitor subTask = subject.newSubTask("SubTask:" + i, STEP_SIZE);
			final int SUB_STEPS = 3;
			subTask.setSize(SUB_STEPS);
			try(subTask) {
				for (int j = 0; j < SUB_STEPS + 2; j++) {
					subTask.worked(1);
				}
			}
			subTask.done();
		}
		// log some extra stuff on the root monitor
		subject.worked(5);
		subject.done();
		subject.worked(5);

		assertEquals(51, recording.size());
		for(int i = 0; i < 50; i+=5) {
			for (int j =0; j < 3; j++) {
				ProgressMonitorEvent evt = recording.get(i + j).event;
				assertEquals(1, evt.getSource().getContext().size()); // depth 1
				assertEquals(ProgressUpdateType.WORK, evt.getType());
			}
			ProgressMonitorEvent evt = recording.get(i + 3).event;
			assertEquals(1, evt.getSource().getContext().size()); // depth 1
			assertEquals(ProgressUpdateType.DONE, evt.getType());
			evt = recording.get(i + 4).event;
			assertEquals(0, evt.getSource().getContext().size()); // depth 1
			assertEquals(ProgressUpdateType.WORK, evt.getType());
		}
		ProgressMonitorEvent evt = recording.get(50).event;
		assertEquals(0, evt.getSource().getContext().size()); // depth 1
		assertEquals(ProgressUpdateType.DONE, evt.getType());
	}



	final int NUM_THREADS = 5;
	final int NUM_STEPS = 10;
	/**
	 * Concurrency test - hitting the same progress from multiple threads does not yield bad progress
	 */
	@Test
	public void simpleConcurrencyTest() throws ExecutionException, InterruptedException {
		// this gives us a pool of threads
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ArrayList<Future<?>> futures = new ArrayList<>();
		// simple work updated from multiple threads
		subject.setSize(NUM_STEPS * NUM_THREADS);
		// create NUM_THREADS * NUM_STEPS tasks and submit to executor
		for(int i = 0; i < NUM_THREADS; i ++) {
			for (int j = 0; j < NUM_STEPS; j++) {

				Future<?> future = executor.submit(new Runnable() {
					@Override
					public void run() {
						subject.worked(1);
						System.out.println(subject + " by "+ Thread.currentThread().getName());
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							// ignore
						}
					}
				});
				futures.add(future);
			}
		}
		for(Future<?> future : futures) {
			future.get();
		}
		// all done
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertTrue(subject.isDone());

		assertEquals(51, recording.size());
	}

	/**
	 * Concurrency test - hitting the sub task progress from multiple threads does not yield bad progress
	 * in parent
	 */
	@Test
	public void subTaskConcurrencyTest() throws ExecutionException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ArrayList<Future<ProgressMonitor>> futures = new ArrayList<>();
		subject.setSize(NUM_THREADS);
		// simple fractionWorked updated from multiple threads
		for(int i = 0; i < NUM_THREADS; i ++) {
			Future<ProgressMonitor> future = executor.submit(() -> {
				ProgressMonitor split = subject.newSubTask(Thread.currentThread().getName());
				split.setSize(NUM_STEPS);
				for (int k = 0; k < NUM_STEPS; k++) {
					split.worked(1L);
				}
				System.out.println(Thread.currentThread().getName() + " DONE @ "+split.getFractionDone());
				split.done();
				return split;
			});
			futures.add(future);

		}
		for(Future<ProgressMonitor> future : futures) {
			ProgressMonitor split = future.get();
			assertEquals(1.0, split.getFractionDone(), 0.001);
			assertTrue(split.isDone());
		}
		// all done
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertTrue(subject.isDone());
	}
}
