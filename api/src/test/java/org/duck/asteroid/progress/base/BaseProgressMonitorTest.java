package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class BaseProgressMonitorTest {

	private BaseProgressMonitor subject = null;
	
	@Before
	public void setUp() throws Exception {
		subject = new BaseProgressMonitor();
	}

	@After
	public void tearDown() throws Exception {
		subject = null;
	}

	/**
	 * When we update the simple (0..1) progress things behave the way we expect
	 */
	@Test
	public void simpleUpdateTest() {
		assertEquals(0.0, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertFalse(subject.isWorkComplete());

		subject.fractionWorked(0.5);
		assertEquals(0.5, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertFalse(subject.isWorkComplete());

		subject.fractionWorked(0.5);
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertTrue(subject.isWorkComplete());

		subject.fractionWorked(0.5);
		assertEquals(1.5, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertTrue(subject.isWorkComplete());

		// rest
		subject.setFractionDone(0.0);
		assertEquals(0.0, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertFalse(subject.isWorkComplete());

		subject.done();
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertTrue(subject.isWorkComplete());

		subject.setCancelled(true);
		assertTrue(subject.isCancelled());
	}

	/**
	 * When we update a simple (0..1) sub task the child and parent remain in sync
	 *
	 */
	@Test
	public void simpleSubTaskTest() {
		ProgressMonitor half = subject.split(0.5, "Half");
		// first update the subject
		// Updating the parent does not update the child
		subject.fractionWorked(0.5);
		assertEquals(0.5, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertFalse(subject.isWorkComplete());
		// sub task untouched
		assertEquals(0.0, half.getFractionDone(), 0.0001);
		assertFalse(half.isCancelled());
		assertFalse(half.isWorkComplete());
		// cancel passes through from parent to child
		subject.setCancelled(true);
		assertTrue(subject.isCancelled());
		assertTrue(half.isCancelled());
		subject.setCancelled(false);
		assertFalse(subject.isCancelled());
		assertFalse(half.isCancelled());

		// does the sub task update teh parent though
		// the sub task is half the parent, and we are doing half of that
		// i.e. 0.25 on the parent
		// but we have already done 0.5 !
		half.fractionWorked(0.5);
		assertEquals(0.5, half.getFractionDone(), 0.0001);
		assertFalse(half.isCancelled());
		assertFalse(half.isWorkComplete());
		assertEquals(0.75, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertFalse(subject.isWorkComplete());

		// if we complete sub task - it complete the parent as appropriate
		subject.setFractionDone(0.0); // put parent progress back
		// completeing the child will not complete the parent
		half.fractionWorked(0.5);
		assertEquals(1.0, half.getFractionDone(), 0.0001);
		assertFalse(half.isCancelled());
		assertTrue(half.isWorkComplete());
		assertEquals(0.25, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertFalse(subject.isWorkComplete());

		// in this case completing the child does complete the parent
		half.setFractionDone(0.0);
		subject.setFractionDone(0.5);
		half.setFractionDone(1.0);
		assertEquals(1.0, half.getFractionDone(), 0.0001);
		assertFalse(half.isCancelled());
		assertTrue(half.isWorkComplete());
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertFalse(subject.isCancelled());
		assertTrue(subject.isWorkComplete());
	}

	/**
	 * Verify that split sub tasks don't interact with each other - only with the parent
	 */
	@Test
	public void subTaskInteractionTest() {
		ProgressMonitor oneHalf = subject.split(0.5, "One half");
		ProgressMonitor otherHalf = subject.split(0.5, "The other half");

		oneHalf.fractionWorked(0.5);
		assertEquals(0.25, subject.getFractionDone(), 0.0001);
		assertEquals(0.0, otherHalf.getFractionDone(), 0.0001);

		otherHalf.fractionWorked(0.5);
		assertEquals(0.5, subject.getFractionDone(), 0.0001);
		assertEquals(0.5, oneHalf.getFractionDone(), 0.0001);


		oneHalf.fractionWorked(0.5);
		assertEquals(1.0, oneHalf.getFractionDone(), 0.0001);
		assertTrue(oneHalf.isWorkComplete());
		assertEquals(0.75, subject.getFractionDone(), 0.0001);
		assertEquals(0.5, otherHalf.getFractionDone(), 0.0001);
		assertFalse(subject.isWorkComplete());

		otherHalf.fractionWorked(0.5);
		assertEquals(1.0, otherHalf.getFractionDone(), 0.0001);
		assertTrue(otherHalf.isWorkComplete());
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertEquals(1.0, otherHalf.getFractionDone(), 0.0001);
		assertTrue(subject.isWorkComplete());
	}

	/**
	 * Creating a split with a total of zero does not result in Maths errors
	 */
	@Test
	public void testZeroSizeSubTask() {
		ProgressMonitor nothing_to_do = subject.split(0.0, "Nothing to do");
		assertEquals(0.0, nothing_to_do.getFractionDone(), 0.0001);
		assertFalse(nothing_to_do.isWorkComplete());
		nothing_to_do.setFractionDone(0.5);
		assertEquals(0.0, subject.getFractionDone(), 0.0001);
		nothing_to_do.setFractionDone(1.0);
		assertEquals(0.0, subject.getFractionDone(), 0.0001);
		assertTrue(nothing_to_do.isWorkComplete());
		assertFalse(subject.isWorkComplete());

	}

	final int NUM_THREADS = 5;
	final int NUM_STEPS = 10;
	/**
	 * Concurrency test - hitting the same progress from multiple threads does not yield bad progress
	 */
	@Test
	public void simpleConcurrencyTest() throws ExecutionException, InterruptedException {
		final double fraction = 1.0 / (NUM_STEPS * NUM_THREADS);
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ArrayList<Future<?>> futures = new ArrayList<>();
		// simple fractionWorked updated from multiple threads
		for(int i = 0; i < NUM_THREADS; i ++) {
			for (int j = 0; j < NUM_STEPS; j++) {
				Future<?> future = executor.submit(new Runnable() {
					@Override
					public void run() {
						subject.fractionWorked(fraction);
						System.out.println(subject.getFractionDone() + " by "+ Thread.currentThread().getName());
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
		assertTrue(subject.isWorkComplete());
	}

	/**
	 * Concurrency test - hitting the sub task progress from multiple threads does not yield bad progress
	 */
	@Test
	public void subTaskConcurrencyTest() throws ExecutionException, InterruptedException {
		final double fraction = 1.0 / NUM_THREADS;
		final double stepWork = 1.0 / NUM_STEPS;
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ArrayList<Future<?>> futures = new ArrayList<>();
		// simple fractionWorked updated from multiple threads
		for(int i = 0; i < NUM_THREADS; i ++) {
			Future<?> future = executor.submit(new Runnable() {
				@Override
				public void run() {
					ProgressMonitor split = subject.split(fraction, Thread.currentThread().getName());
					for (int k = 0; k < NUM_STEPS; k++) {
						split.fractionWorked(stepWork);
						System.out.println("split="+split.getFractionDone() +"/"+fraction+", subject=" +subject.getFractionDone() + " by "+ Thread.currentThread().getName());
					}
					System.out.println(Thread.currentThread().getName() + " DONE @ "+split.getFractionDone());
					split.done();
					assertEquals(1.0, split.getFractionDone(), 0.001);
					assertTrue(split.isWorkComplete());
				}
			});
			futures.add(future);

		}
		for(Future<?> future : futures) {
			future.get();
		}
		// all done
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertTrue(subject.isWorkComplete());
	}
}
