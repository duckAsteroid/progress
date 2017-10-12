package org.duck.asteroid.progress.base.frac;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.base.AbstractProgressMonitor;

import java.util.concurrent.atomic.AtomicLong;

public class FractionalProgressLong extends AbstractFractionalProgress<Long> implements FractionalProgress<Long> {

    private final AtomicLong work = new AtomicLong(0L);

    public FractionalProgressLong(final AbstractProgressMonitor delegate, final long total) {
        super(delegate, total);
    }


    @Override
    public Long getWorkDone() {
        return work.get();
    }

    @Override
    public Long getWorkRemaining() {
        return total.longValue() - work.get();
    }

    @Override
    public void setWorkDone(Long done) {
        work.set(done);
        super.setFractionDone(done.doubleValue() / total.doubleValue());
    }

    @Override
    public void setWorkRemaining(Long remaining) {
        setWorkDone(total - remaining);
    }

    @Override
    public void incrementWork() {
        worked(1L, null);
    }

    @Override
    public void worked(Long amount) {
        worked(amount, null);
    }

    @Override
    public void worked(Long amount, String status) {
        boolean updated;
        long targetWorked;
        do {
            long worked = work.get();
            targetWorked = worked + amount;
            // if someone updated work while we were computing the new value - try again...
            updated = work.compareAndSet(worked, targetWorked);
        } while(!updated);
        // do we need to post status
        if (status != null) {
            notify(status);
        }
        // calc fraction work and propogate
        super.fractionWorked(amount.doubleValue() /  total.doubleValue());
    }

    @Override
    public void done() {
        work.set(total);
        // notify delegate of new work
        super.setFractionDone(1.0);
    }


    @Override
    public void setFractionDoneInternal(double fractionDone,  AbstractProgressMonitor ignored) {
        Double doubleWork = total.doubleValue() * fractionDone;
        work.set(doubleWork.intValue());
        // DO NOT pass to delegate - it passed it to us!
    }


}
