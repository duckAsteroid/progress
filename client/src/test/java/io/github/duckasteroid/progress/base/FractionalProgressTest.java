package io.github.duckasteroid.progress.base;

import io.github.duckasteroid.progress.ProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FractionalProgressTest {
    private BaseProgressMonitor subject;
    @Before
    public void setUp() throws Exception {
        subject = new BaseProgressMonitor();
    }

    @After
    public void tearDown() throws Exception {
        subject = null;
    }

    @Test
    public void simpleFractionTest() {
        subject.setSize(10);
        FractionAssert assertThat = FractionAssert.on(subject);
        assertThat.expectedWorkDone(0).check();

    }



    final int TOTAL = 1000;
    final int NUM_STEPS = 100;
    final int NUM_THREADS = 10;

    /**
     * Concurrency test - hitting the same progress from multiple threads
     */
    @Test
    public void singleProgressConcurrencyTest() throws ExecutionException, InterruptedException {
        final int step = TOTAL / NUM_STEPS / NUM_THREADS;
        subject.setSize(TOTAL);
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        // simple fractionWorked updated from multiple threads
        for(int i = 0; i < NUM_THREADS; i ++) {
            Future<Integer> future = executor.submit(new Callable<Integer>() {
                int counter = 0;
                @Override
                public Integer call() {
                    for (int j = 0; j < NUM_STEPS; j++) {
                        String threadName = Thread.currentThread().getName();
                        counter += step;
                        subject.worked(step, threadName);
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            // nothing to do
                            break;
                        }
                        System.out.println(subject.getWorkDone() + "["+counter+"] by " + threadName);
                    }
                    return counter;
                }
            });
            futures.add(future);
        }
        for(Future<Integer> future : futures) {
            int count = future.get();
            assertEquals(TOTAL / NUM_THREADS, count);
        }
        // all done

        assertEquals(1.0, subject.getFractionDone(), 0.0001);

        FractionAssert assertFrac = FractionAssert.on(subject);
        assertFrac.expectedWorkDone(TOTAL).check();

        assertTrue(subject.isDone());
    }

    /**
     * Concurrency test with sub tasks
     */
    @Test
    public void concurrentSubTaskTest() throws ExecutionException, InterruptedException {
        final long MASTER_WORK = 10000;
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        ArrayList<Future<?>> futures = new ArrayList<>();
        subject.setSize(NUM_THREADS);
        // simple fractionWorked updated from multiple threads
        for(int i = 0; i < NUM_THREADS; i ++) {
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    ProgressMonitor subTask = subject.newSubTask(Thread.currentThread().getName());
                    subTask.setSize(NUM_STEPS);
                    for (int k = 0; k < NUM_STEPS; k++) {
                        subTask.worked(1L);
                        System.out.println("subTask="+subTask);
                    }
                    System.out.println(Thread.currentThread().getName() + " DONE @ "+ subject);
                    subTask.done();
                    FractionAssert assertSplit = FractionAssert.on(subTask);
                    assertSplit.expectedWorkDone((long)NUM_STEPS).check();
                }
            });
            futures.add(future);

        }
        for(Future<?> future : futures) {
            future.get();
        }
        // all done
        FractionAssert assertMaster = FractionAssert.on(subject);
        assertMaster.expectedWorkDone(NUM_THREADS).check();
    }
}
