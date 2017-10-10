package org.duck.asteroid.progress.base.frac;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.AbstractProgressMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A base class for fractional progress monitors - delegates all the {@link ProgressMonitor} methods
 * to a base. Leaving the subclasses to implement just {@link org.duck.asteroid.progress.FractionalProgress}.
 * Fraction calculations (as double) against the total (and subsequent call to base) is implemented here
 * and is also routed to the base.
 */
public abstract class AbstractFractionalProgress<T extends Number> extends AbstractProgressMonitor implements FractionalProgress<T> {
    /** The base progress monitor - all methods routed here */
    private final AbstractProgressMonitor base;
    /** The total (denominator) amount of fractional progress */
    protected final T total;
    /** Tracks the relation between this and base (so this both appear in sub tasks) */
    protected final List<ProgressMonitor> context;

    public AbstractFractionalProgress(AbstractProgressMonitor base, T total) {
        this.base = base;
        this.total = total;
        this.context = new ArrayList<>(base.getContext());
        context.add(base);
    }

    @Override
    public T getTotalWork() {
        return total;
    }

    @Override
    public double getFractionDone() {
        return base.getFractionDone();
    }

    @Override
    public void setFractionDone(double fractionDone) {
        base.setFractionDone(fractionDone);
    }

    @Override
    public void fractionWorked(double amount) {
        base.fractionWorked(amount);
    }

    @Override
    public boolean isWorkComplete() {
        return base.isWorkComplete();
    }

    @Override
    public boolean isCancelled() {
        return base.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        base.setCancelled(cancelled);
    }

    @Override
    public ProgressMonitor getParent() {
        return base;
    }

    @Override
    public List<ProgressMonitor> getContext() {
        return Collections.unmodifiableList(context);
    }

    @Override
    public ProgressMonitor newSubTask(T work, String taskName) {
        double fraction = work.doubleValue() / total.doubleValue();
        return split(fraction, taskName);
    }

    @Override
    public void logUpdate(ProgressMonitor source) {
        base.logUpdate(source);
    }
}
