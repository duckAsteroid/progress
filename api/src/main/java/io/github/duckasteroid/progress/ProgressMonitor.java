package io.github.duckasteroid.progress;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * The interface to an object an application can use to report work on a task.
 * The task has a {@linkplain #getSize() size} - representing the total amount of work to be done.
 * The application then logs {@linkplain #worked(long, String) work} against this (adding an amount
 * of work to the {@linkplain #getWorkDone() work done}. The application may also update the current
 * status message (with or without updating the work done) as a way to indicate the process is
 * ongoing.  When the application has completed the task (regardless of how much work has been
 * logged) it must call {@link #done()} in the case of subtasks - this is necessary to log the work
 * on the parent. A progress monitor is also {@link Closeable} for this purpose (the
 * {@link #close()} method simply calls {@link #done()}) so the client may use a
 * <code>try with</code> construct.
 *
 * <p>Progress monitors incorporate a {@link #isCancelled() cancellation} mechanism that permits the
 * instance owner to send cancellation (stop processing) notification to the receiver.</p>
 *
 * <p>NOTE: Implementations of this interface are intended to be fool proof for use by client code,
 * if they you dumb stuff (e.g. log too much work, call done twice etc.) it should not result in any
 * runtime exception or odd behaviour.</p>
 */
public interface ProgressMonitor extends Comparable<ProgressMonitor>, Closeable {
  /**
   * The parent monitor of this monitor (if any).
   * @return the parent monitor or <code>null</code> if this is the root
   */
  ProgressMonitor getParent();

  /**
   * The path from this monitor to the root through all parent contexts in order leaf to root.
   * @return a list of parent contexts (all the way to the root); an empty list if this is the
   *         root.
   */
  List<ProgressMonitor> getContext();

  /**
   * The name of this task set by the {@link #getParent() parent} (if any).
   * @return the task name (or empty String)
   */
  String getTaskName();

  /**
   * The last {@link #setStatus(String) notified} status.
   * @return the task status (or empty String)
   */
  String getStatus();

  /**
   * Notify users (if possible) of the status of this task (without changing the fraction done).
   * @param status The task status notification message
   */
  void setStatus(String status);

  /**
   * The "size" of this task - by default this is <code>1</code>.
   * @return the "relative" size of this task
   */
  long getSize();

  /**
   * Modify the overall size of this task. If the current {@link #getWorkDone()} is already more
   * than this then the monitor will also be marked {@link #isDone() done}.
   * Changing the size of a "done" monitor has no effect, it remains done...
   * The size cannot be modified to less than 1; the value defaults to this if out of range.
   * Note: modifying the size of a subtask - does not influence the work it contributes to the
   * parent task
   * @param size the new size of this task
   */
  void setSize(long size);

  /**
   * The unit of work in this monitor (default empty).
   * Sub tasks will inherit this unit.
   * @return the unit as set in {@link #setUnit(String)}
   */
  String getUnit();

  /**
   * Modify the unit of this task, only new subtasks will pick up this unit.
   * @param unit the new unit
   */
  void setUnit(String unit);

  /**
   * The current amount of work done. This is always a positive number or zero.
   * It may be more than the size of this task (which simply means the task is done)
   * @return the current amount of work done
   */
  long getWorkDone();

  /**
   * Log an amount of work and (optionally) update the status in a single operation.
   * This operation has no effect if the monitor is {@linkplain #isDone() done} or
   * {@linkplain #isCancelled() cancelled}.
   * If this work takes the {@linkplain #getWorkDone() total work done} past the {@linkplain
   * #getSize() size} then the monitor will be marked done.
   * If the amount of work is less than zero, it will default to zero
   * @param amount the amount of work done
   * @param status a status message to set (if not <code>null</code>)
   * @return the value of {@link #getWorkDone()} as a result of this new work
   */
  long worked(long amount, String status);

  /**
   * Log an amount of work with no (null) corresponding status update.
   * This is the same as calling <code>worked(amount, null)</code>
   * @see #worked(long, String)
   * @param amount the amount of work done
   * @return the value of {@link #getWorkDone()} as a result of this new work
   */
  long worked(long amount);

  /**
   * Called to set the work done to the size of the task (at this point in time).
   * Subsequent modifications to {@link #getSize()} may alter this, but the progress will only be
   * done once
   */
  void done();

  /**
   * Has the work reported completed (i.e. {@link #getWorkDone()} &gt;= {@link #getSize()}} )
   * NOTE: This is a one time event, if the work done is ever &gt;= the size - subsequent
   * increases in the size don't "undo" the monitor, it remains done.
   * @return true if the work was complete/done
   */
  boolean isDone();

  @Override
  default void close() {
    done();
  }

  /**
   * Has the task being reported been cancelled. This is used as a signal between the class doing
   * progress and the outside world that may wish it to stop before it is complete.
   * The cancelled state has no appreciable impact on the rest of the workings of the monitor -
   * it's for the receiver and the publisher of the progress to decide what to do with it.
   * @return true if cancelled
   */
  boolean isCancelled();

  /**
   * Change the cancelled state of the task being monitored (and that of all sub-tasks).
   * @see #isCancelled()
   * @param cancelled the new cancelled state
   */
  void setCancelled(boolean cancelled);

  /**
   * Create a sub task of this - which when {@linkplain #isDone() done} equates to the given
   * amount of work in this monitor.
   * NOTE: The progress of the subtask is not reflected in this monitor until it is
   * {@link #done()}, only then is the total work posted on this monitor.
   * @param name the name of the sub task
   * @param work the amount of work the new sub task will contribute to this monitor when done
   * @return a new progress monitor for the sub task
   * @throws IllegalStateException If this task is already {@link #isDone() done} or is
   *                 {@link #isCancelled() cancelled}
   */
  ProgressMonitor newSubTask(String name, long work);

  /**
   * Create a sub task of this - which when {@link #isDone()} equates to a single (1) unit of work
   * in this task.
   * Equivalent to calling {@link #newSubTask(String, long)} with the given <code>name</code> and
   * a <code>size = 1</code>
   * @param name the name of the sub task
   * @return a new progress monitor for the sub task
   * @throws IllegalStateException If this task is already {@link #isDone() done} or is
   *                 {@link #isCancelled() cancelled}
   */
  ProgressMonitor newSubTask(String name);

  /**
   * The {@link #getWorkDone()} as a fraction of the {@link #getSize()} for this task in the range
   * 0 to 1.
   * NOTE: This value can never be less than 0 or more than 1
   * @return the fraction done
   */
  double getFractionDone();


}
