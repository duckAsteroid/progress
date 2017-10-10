package org.duck.asteroid.progress.base.frac;

import org.duck.asteroid.progress.base.AbstractProgressMonitor;

public class ZeroFraction<T extends Number> extends AbstractFractionalProgress<T> {

    public ZeroFraction(AbstractProgressMonitor parent, T total) {
        super(parent, total);
        parent.done();
    }


    @Override
    public T getWorkDone() {
        return total;
    }

    @Override
    public T getWorkRemaining() {
        return total;
    }

    @Override
    public void setWorkDone(T done) {
        // ignored
        logUpdate(this);
    }

    @Override
    public void setWorkRemaining(T remaining) {
        // ignored
        logUpdate(this);
    }

    @Override
    public void worked(T work, String status) {
        // ignored
        notify(status);
        logUpdate(this);
    }

    @Override
    public void worked(T work) {
        // ignored
        logUpdate(this);
    }

    @Override
    public void incrementWork() {
        // ignored
        logUpdate(this);
    }

    @Override
    public void done() {
        // ignored
        logUpdate(this);
    }

    @Override
    public void setFractionDoneInternal(double fractionDone, AbstractProgressMonitor ignored) {
        // ignored
    }
}
