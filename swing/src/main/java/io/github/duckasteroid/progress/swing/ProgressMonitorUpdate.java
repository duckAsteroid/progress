package io.github.duckasteroid.progress.swing;


import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;

import java.awt.*;

public class ProgressMonitorUpdate implements ProgressMonitorListener {
    private transient final javax.swing.ProgressMonitor swing;
    private transient final BaseProgressMonitor progress;

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
