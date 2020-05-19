package org.duck.asteroid.progress.swing;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressMonitorEvent;
import org.duck.asteroid.progress.base.event.ProgressMonitorListener;

import javax.swing.*;

public class JProgressBarModel extends DefaultBoundedRangeModel implements ProgressMonitorListener {
    private String status;

    public static ProgressMonitor newMonitor(JProgressBar bar, String taskName, int totalWork) {
        BaseProgressMonitor monitor = new BaseProgressMonitor(taskName, totalWork);
        JProgressBarModel model = new JProgressBarModel();
        monitor.addProgressMonitorListener(model);
        bar.setModel(model);
        return monitor;
    }

    @Override
    public void logUpdate(ProgressMonitorEvent event) {
        setMaximum((int)event.getRoot().getSize());
        setValue((int)event.getRoot().getWorkDone());
        this.status = event.getSource().getStatus();
    }

    public String getStatus() {
        return status;
    }
}
