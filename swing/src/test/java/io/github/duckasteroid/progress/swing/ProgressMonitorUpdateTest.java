package io.github.duckasteroid.progress.swing;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

public class ProgressMonitorUpdateTest extends JPanel
        implements ActionListener,
        PropertyChangeListener {

    private io.github.duckasteroid.progress.ProgressMonitor progressMonitor;
    private javax.swing.ProgressMonitor swingMonitor;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;

    class Task extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            setProgress(0);
            try {
                Thread.sleep(1000);
                while (progress < 100 && !isCancelled()) {
                    //Sleep for up to one second.
                    Thread.sleep(random.nextInt(1000));
                    //Make random progress.
                    progress += random.nextInt(10);
                    setProgress(Math.min(progress, 100));
                }
            } catch (InterruptedException ignore) {}
            return null;
        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            progressMonitor.done();
        }
    }

    public ProgressMonitorUpdateTest() {
        super(new BorderLayout());

        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        add(startButton, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }


    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        ProgressMonitorUpdate update = new ProgressMonitorUpdate(ProgressMonitorUpdateTest.this,
                "Running a Long Task",
                100);
        progressMonitor = update.getProgressMonitor();
        swingMonitor = update.getSwingMonitor();

        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
        startButton.setEnabled(false);
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName() ) {
            int work = (Integer) evt.getNewValue() - (int)progressMonitor.getWorkDone();
            progressMonitor.worked(work);
            String message =
                    String.format("Did another %d%%.\n", work);
            progressMonitor.setStatus(message);
            taskOutput.append(message);
            if (progressMonitor.isCancelled() || task.isDone()) {
                Toolkit.getDefaultToolkit().beep();
                if (progressMonitor.isCancelled()) {
                    task.cancel(true);
                    taskOutput.append("Task canceled.\n");
                } else {
                    taskOutput.append("Task completed.\n");
                }
                startButton.setEnabled(true);
            }
        }

    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ProgressMonitorUpdateTest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ProgressMonitorUpdateTest();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}