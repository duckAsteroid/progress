package org.duck.asteroid.progress.base.frac;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.base.AbstractProgressMonitor;

import java.util.concurrent.atomic.AtomicInteger;

public class FractionalProgressInteger extends AbstractFractionalProgress<Integer> implements FractionalProgress<Integer> {
    
    private final AtomicInteger work = new AtomicInteger(0);
    
    public FractionalProgressInteger(final AbstractProgressMonitor delegate, final Integer total) {
        super(delegate, total);
    }


    @Override
    public Integer getWorkDone() {
        return work.get();
    }

    @Override
    public Integer getWorkRemaining() {
        return total.intValue() - work.get();
    }

    @Override
    public void setWorkDone(Integer done) {
        work.set(done);
        super.setFractionDone(done.doubleValue() / total.doubleValue());
    }

    @Override
    public void setWorkRemaining(Integer remaining) {
        setWorkDone(total - remaining);
    }

    @Override
    public void incrementWork() {
        worked(1, null);
    }
    
    @Override
    public void worked(Integer amount) {
        worked(amount, null);
    }

    @Override
    public void worked(Integer amount, String status) {
        double fractionWorkAmount = amount.doubleValue() / total.doubleValue();
        boolean updated;
        int targetWorked;
        do {
            int worked = work.get();
            targetWorked = worked + amount;
            // if someone updated work while we were computing the new value - try again...
            updated = work.compareAndSet(worked, targetWorked);
        } while(!updated);
        // do we need to post status
        if (status != null) {
            notify(status);
        }
        // calc fraction work and propogate
        super.fractionWorked(fractionWorkAmount);
    }

    @Override
    public void done() {
        work.set(total);
        // notify delegate of new work
        super.setFractionDone(1.0);
    }

    @Override
    public void setFractionDoneInternal(double fractionDone, AbstractProgressMonitor ignored) {
        Double doubleWork = total.doubleValue() * fractionDone;
        work.set(doubleWork.intValue());
        // DO NOT pass to delegate - it passed it to us!
    }
}
