package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.FractionalProgress;

import static org.junit.Assert.assertEquals;

/**
 * A helper for doing assertions on a {@link FractionalProgress}
 * @param <T>
 */
public class FractionAssert<T extends Number> {
    private final T total;
    private T work;
    private FractionalProgress<T> progress;

    private FractionAssert(T total) {
        this.total = total;
    }

    public static <T extends Number> FractionAssert<T> outOf(T total) {
        return new FractionAssert<>(total);
    }

    public static <T extends Number> FractionAssert<T> on(FractionalProgress<T> progress) {
        return new FractionAssert<>(progress.getTotalWork()).with(progress);
    }

    public FractionAssert<T> expectedWorkDone(T worked) {
        this.work = worked;
        return this;
    }

    public FractionAssert<T> with(FractionalProgress<T> progress) {
        this.progress = progress;
        return this;
    }

    /**
     * Given the state of the expected work done - validate the values of the progress
     */
    public FractionAssert<T> check(double fraction) {
        assertEquals(total.longValue(), progress.getTotalWork().longValue());
        assertEquals(work.longValue(), progress.getWorkDone().longValue());
        if (total.longValue() != 0) {
            long remaining = total.longValue() - work.longValue();
            assertEquals(remaining, progress.getWorkRemaining().longValue());
        }
        assertEquals(fraction, progress.getFractionDone(), 0.0001);
        boolean complete = fraction >= 1.0d;
        assertEquals(complete, progress.isWorkComplete());
        return this;
    }

    public FractionAssert<T> check() {
        if (total.longValue() != 0) {
            double fraction = work.doubleValue() / total.doubleValue();
            return check(fraction);
        }
        else {
            return check(1.0);
        }
    }
}
