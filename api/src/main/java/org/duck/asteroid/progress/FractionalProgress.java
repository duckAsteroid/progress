package org.duck.asteroid.progress;

/**
 * A progress monitor that tracks a fractional amount of work and wraps a simple {@link ProgressMonitor}
 * @param <T> The type of number in the fraction
 */
public interface FractionalProgress<T extends Number> extends ProgressMonitor {
    /**
     * The total amount work that this task entails.
     * This value is read only.
     * The value is provided in {@link ProgressMonitor#asInteger(int)} or {@link ProgressMonitor#asLong(long)}
     * @return The total work. A positive number
     */
    T getTotalWork();

    /**
     * The work done so far. This starts at zero and is increased until it equals {@link #getTotalWork()}.
     * It is never more than {@link #getTotalWork()}
     * @return The work done so far. A positive integer.
     */
    T getWorkDone();

    /**
     * The remaining work to be done. This is {@link #getTotalWork()} minus {@link #getWorkDone()}
     * @return The work remaining
     */
    T getWorkRemaining();

    /**
     * Set the absolute work done so far.
     * This can be less than the current {@link #getWorkDone()}.
     * Attempting to set this value larger than {@link #getTotalWork()} will result
     * in {@link #getWorkDone()} == {@link #getTotalWork()}.
     * Attempting to set this value to less than zero will result in {@link #getWorkDone()} == 0.
     * @param done The work done so far.
     */
    void setWorkDone(T done);



    /**
     * Set the absolute work remaining. This is only permitted to reduce; it cannot rise (you can do this with
     * {@link #setWorkDone(Number)}).
     * Attempts to set the remaining work more than the {@link #getWorkRemaining() current remaining value}, are
     * ignored.
     * Attempts to set the remaining work to a negative value are ignored.
     * Attempts to set the remaining work to be more than the {@link #getTotalWork() total work} are ignored.
     * @param remaining The new amount of work remaining.
     */
    void setWorkRemaining(T remaining);

    /**
     * Log an amount of work being done - with an (optional) status message.
     * This is broadly similar to calling {@link #setWorkDone(Number)} with {@link #getWorkDone()} + the amount of work.
     * However internally this operation is atomic; and will not result in work done being updated between calls.
     * @param work The work that has been done.
     * @param status A new status message (or null to leave as is)
     */
    void worked(T work, String status);
    /**
     * Log an amount of work being done.
     * This is broadly similar to calling {@link #setWorkDone(Number)} with {@link #getWorkDone()} + the amount of work.
     * However internally this operation is atomic; and will not result in work done being updated between calls.
     * @param work The work that has been done.
     */
    void worked(T work);

    /**
     * Incremement the work by one. This is the same as calling {@link #worked(Number)} with the value of one.
     */
    void incrementWork();

    /**
     * Add a sub task to this task. When completed this sub task will contribute the given amount of
     * work to this task.
     * @param work The amount of work the sub task will contribute to this monitor
     * @param taskName The name of the task. Or <code>null</code>
     * @return A new sub task object
     */
    ProgressMonitor newSubTask(T work, String taskName);
}
