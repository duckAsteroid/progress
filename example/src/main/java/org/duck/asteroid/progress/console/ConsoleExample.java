package org.duck.asteroid.progress.console;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.ProgressMonitorFactory;
import org.duck.asteroid.progress.base.format.CompoundFormat;

import java.util.Date;
import java.util.Random;

/**
 * An example using the console progress
 */
public class ConsoleExample {

    private static Random rnd = new Random();

    public static void main(String[] args)  {
        boolean multiline = args.length > 0 ? Boolean.parseBoolean(args[0]) : true;
        int delay = args.length > 1 ? Integer.parseInt(args[1]) : 250;
        ProgressMonitor monitor = ProgressMonitorFactory.newMonitor(ConsoleExample.class.getName(), 2);
        for(int i = 0; i < 2; i ++) {
            ProgressMonitor subTask = monitor.newSubTask("L1:"+i, 1);
            subTask.setSize(3);
            for(int j = 0; j < 3; j ++) {
                ProgressMonitor subSubTask = subTask.newSubTask("L2:"+i+"."+j, 1);
                doWork(4, 1, delay, subSubTask);
            }
        }
        monitor.done();
    }

    public static void doWork(int work, int stepSize, int sleep, ProgressMonitor monitor) {
        monitor.setSize(work);
        for (int j = 0; j < work; j+=stepSize) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                break;
            }
            if (rnd.nextBoolean()) {
                monitor.setStatus("Just a status message, not progress");
            }
            monitor.worked(stepSize, new Date().toString());

        }
        monitor.done();
    }


}