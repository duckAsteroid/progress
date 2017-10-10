package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.FractionalProgress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FractionalProgressTest {
    private BaseProgressMonitor subject;
    private FractionalProgress<Integer> tenths;
    private FractionAssert<Integer> assertTenths;
    private FractionalProgress<Long> thousandths;
    private FractionAssert<Long> assertThousandths;

    @Before
    public void setUp() throws Exception {
        subject = new BaseProgressMonitor();
        tenths = subject.asInteger(10);
        assertTenths = FractionAssert.on(tenths);
        thousandths = subject.asLong(1000);
        assertThousandths = FractionAssert.on(thousandths);
    }

    @After
    public void tearDown() throws Exception {
        subject = null;
        tenths = null;
        assertTenths = null;
        thousandths = null;
        assertThousandths = null;
    }

    @Test
    public void simpleFractionTest() {
        assertTenths.expectedWorkDone(0).check();
        tenths.worked(1);
        assertTenths.expectedWorkDone(1).check();
        tenths.setWorkDone(5);
        assertTenths.expectedWorkDone(5).check();
        tenths.setWorkRemaining(2);
        assertTenths.expectedWorkDone(8).check();
    }

    /**
     * When we update a fractional (int) progress the root and derived progress(es) remain in sync
     * we may update the root or the derived progress
     */
    @Test
    public void testSimpleFractionalSync() {
        // update the root (to half)
        subject.setFractionDone(0.5);
        // we are at 5 / 10ths
        assertTenths.expectedWorkDone(5).check();
        // and 500 / 1000ths
        assertThousandths.expectedWorkDone(500L).check();

        // update tenths via work done 6 / 10 (=0.6)
        tenths.setWorkDone(6);
        assertEquals(0.6, subject.getFractionDone(), 0.001);
        assertThousandths.expectedWorkDone(600L).check();

        // update thousands by 1 601 / 1000
        thousandths.worked(1L);
        assertTenths.expectedWorkDone(6).check(0.601);
        assertThousandths.expectedWorkDone(601L).check();
        assertEquals(0.6 + (1.0 / 1000.0), subject.getFractionDone(), 0.0001);
    }

    /**
     * When we update a sub task fractional (int) the parent is updated correctly (e.g. child started at 0.5)
     */
    @Test
    public void subTaskFractionTest() {
        // divide subject into two halfs
        FractionalProgress<Integer> two = subject.asInteger(2);
        FractionAssert<Integer> assertTwo = FractionAssert.on(two);
        // do one half...
        two.newSubTask(1, "One half").done();
        assertTwo.expectedWorkDone(1).check();

        // we are now at 0.5
        assertEquals(0.5, subject.getFractionDone(), 0.0001);
        // we are going to divide the last half into 100000 pieces...
        final int bigWork = 100000;
        FractionalProgress<Integer> other_half = two.newSubTask(1, "Other half").asInteger(bigWork);
        FractionAssert<Integer> assertOtherHalf = FractionAssert.on(other_half);
        // do 1 / 100000 th..
        other_half.worked(1);
        assertOtherHalf.expectedWorkDone(1).check();
        // check the tiny amount of extra progress on the subject
        double worked = 1.0 / (double) bigWork;
        assertEquals(0.5 + worked, subject.getFractionDone(), 0.0001);

        // now we set last half (100000) to done...
        other_half.done();
        assertOtherHalf.expectedWorkDone(bigWork).check();
        assertTrue(other_half.isWorkComplete());
        assertTrue(subject.isWorkComplete());
    }

    /**
     * When we update a sub task fractional (int) the parent and any fractional derived from the parent are updated
     */
    @Test
    public void testSubTaskThroughParentToDerived() {
        FractionalProgress<Integer> tenths = subject.asInteger(10);
        FractionAssert<Integer> assertTenths = FractionAssert.on(tenths);
        // sub task = 5/10 (/50)
        FractionalProgress<Integer> half = tenths.newSubTask(5, "half").asInteger(50);
        FractionAssert<Integer> assertHalf = FractionAssert.on(half);
        half.worked(10);
        assertHalf.expectedWorkDone(10).check();

        // check the derived on the parent
        assertTenths.expectedWorkDone(1).check();
    }

    /**
     * Creating a fraction with a denominator of zero does not cause maths errors
     */
    @Test
    public void zeroSizeFraction() {
        FractionalProgress<Integer> zeroInt = subject.asInteger(0);
        assertNotNull(zeroInt);
        FractionAssert<Integer> assertInt = FractionAssert.on(zeroInt);
        assertInt.expectedWorkDone(0).check();
        FractionalProgress<Long> zeroLong = subject.asLong(0L);
        FractionAssert<Long> assertLong = FractionAssert.on(zeroLong);
        assertLong.expectedWorkDone(0L).check();
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
        FractionalProgress<Integer> fractionalProgress = subject.asInteger(TOTAL);
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
                        fractionalProgress.worked(step, threadName);
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            // nothing to do
                            break;
                        }
                        System.out.println(fractionalProgress.getWorkDone() + "["+counter+"] by " + threadName);
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

        FractionAssert<Integer> assertFrac = FractionAssert.on(fractionalProgress);
        assertFrac.expectedWorkDone(TOTAL).check();

        assertTrue(subject.isWorkComplete());
    }

    /**
     * Concurrency test with sub tasks
     */
}
