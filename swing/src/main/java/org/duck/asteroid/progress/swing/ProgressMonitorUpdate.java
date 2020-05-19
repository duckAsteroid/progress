package org.duck.asteroid.progress.swing;


import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorEvent;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;

import java.awt.*;

public class ProgressMonitorUpdate implements ProgressMonitorListener {
    private final javax.swing.ProgressMonitor swing;
    private final BaseProgressMonitor progress;

    public ProgressMonitorUpdate(Component parent, String message, int totalWork) {
        this.swing = new javax.swing.ProgressMonitor(parent, message, "", 0, totalWork);
        this.progress = new BaseProgressMonitor(message, totalWork);
        this.progress.addProgressMonitorListener(this);
    }

    public javax.swing.ProgressMonitor getSwingMonitor() {
        return this.swing;
    }

    public ProgressMonitor getProgressMonitor() {
        return progress;
    }

    @Override
    public void logUpdate(ProgressMonitorEvent event) {
        swing.setMaximum((int)event.getRoot().getSize());
        swing.setProgress((int)event.getRoot().getWorkDone());
        swing.setNote(event.getSource().getStatus());
    }
}
