package org.duck.asteroid.progress.base.frac;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.AbstractProgressMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A base class for fractional progress monitors - delegates all the {@link ProgressMonitor} methods
 * to a delegate. Leaving the subclasses to implement just {@link org.duck.asteroid.progress.FractionalProgress}.
 * Fraction calculations (as double) against the total (and subsequent call to delegate) is implemented here
 * and is also routed to the delegate.
 */
public abstract class AbstractFractionalProgress<T extends Number> extends AbstractProgressMonitor implements FractionalProgress<T> {
    /** The delegate progress monitor - all methods routed here */
    private final AbstractProgressMonitor delegate;
    /** The total (denominator) amount of fractional progress */
    protected final T total;

    protected final List<ProgressMonitor> context;

    public AbstractFractionalProgress(AbstractProgressMonitor delegate, T total) {
        this.delegate = delegate;
        this.total = total;
        this.context = new ArrayList<>(delegate.getContext());
        context.add(delegate);
    }

    @Override
    public T getTotalWork() {
        return total;
    }

    protected void updateDelegate(Number newWork) {
        // work out the fraction
        double fraction = newWork.doubleValue() / total.doubleValue();
        // tell the delegate
        delegate.setFractionDone(fraction);
    }

    @Override
    public double getFractionDone() {
        return delegate.getFractionDone();
    }

    @Override
    public void setFractionDone(double fractionDone) {
        delegate.setFractionDone(fractionDone);
    }

    @Override
    public boolean isWorkComplete() {
        return delegate.isWorkComplete();
    }

    @Override
    public void done() {
        delegate.done();
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        delegate.setCancelled(cancelled);
    }

    @Override
    public ProgressMonitor getParent() {
        return delegate;
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
        delegate.logUpdate(source);
    }
}
