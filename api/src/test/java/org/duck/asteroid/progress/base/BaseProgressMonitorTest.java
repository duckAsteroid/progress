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
		subject = new BaseProgressMonitor(100);
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
		FractionAssert fractionAssert = FractionAssert.on(subject);
		fractionAssert.expectedWorkDone(0).check();

		subject.worked(50L);
		fractionAssert.expectedWorkDone(50).check();

		subject.worked(50L);
		fractionAssert.expectedWorkDone(100).check();

		subject.worked(50L);
		fractionAssert.expectedWorkDone(150).check();
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
	}

	/**
	 * Concurrency test - hitting the sub task progress from multiple threads does not yield bad progress
	 * in parent
	 */
	@Test
	public void subTaskConcurrencyTest() throws ExecutionException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		ArrayList<Future<?>> futures = new ArrayList<>();
		subject.setSize(NUM_THREADS);
		// simple fractionWorked updated from multiple threads
		for(int i = 0; i < NUM_THREADS; i ++) {
			Future<?> future = executor.submit(new Runnable() {
				@Override
				public void run() {
					ProgressMonitor split = subject.newSubTask(Thread.currentThread().getName());
					split.setSize(NUM_STEPS);
					for (int k = 0; k < NUM_STEPS; k++) {
						split.worked(1L);
					}
					System.out.println(Thread.currentThread().getName() + " DONE @ "+split.getFractionDone());
					split.done();
					assertEquals(1.0, split.getFractionDone(), 0.001);
					assertTrue(split.isDone());
				}
			});
			futures.add(future);

		}
		for(Future<?> future : futures) {
			future.get();
		}
		// all done
		assertEquals(1.0, subject.getFractionDone(), 0.0001);
		assertTrue(subject.isDone());
	}
}
