package io.github.duckasteroid.progress.base;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import io.github.duckasteroid.progress.base.event.ProgressUpdateType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A root implementation of the progress monitor interface.
 * Capable of creating sub tasks as required.
 * Maintains and notifies a list of listeners.
 */
public final class BaseProgressMonitor extends AbstractProgressMonitor implements ProgressMonitor {

	/** Has cancellation been requested */
	protected AtomicBoolean cancelled = new AtomicBoolean(false);

	/** A set of listeners */
	protected final List<ProgressMonitorListener> listeners = new CopyOnWriteArrayList<>();

	public BaseProgressMonitor() {
		this("");
	}

	public BaseProgressMonitor(final String name) {
		super(0, name);
	}

	public BaseProgressMonitor(final String name, final long size) {
		this(name);
		this.setSize(size);
	}

	public BaseProgressMonitor(final String name, final long size, Collection<ProgressMonitorListener> listeners) {
		this(name);
		this.setSize(size);
		this.listeners.addAll(listeners);
	}

	public BaseProgressMonitor(final long size) {
		this();
		this.setSize(size);
	}

	public void addProgressMonitorListener(ProgressMonitorListener listener) {
		listeners.add(listener);
	}

	public void removeProgressMonitorListener(ProgressMonitorListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void notifyListeners(final ProgressMonitor source, final ProgressUpdateType updateType) {
		final ProgressMonitorEvent event = new ProgressMonitorEvent(this, source, updateType);
		// iterate the listeners (if any)
		for(ProgressMonitorListener listener : listeners) {
			listener.logUpdate(event);
		}
	}

	@Override
	protected void onDone() {
		// nothing to do!
	}

	/**
	 * A list of all the active monitors in this monitor and all children that are active.
	 * As soon as a monitor is marked done - all children are removed from this list.
	 * @return the list of all active monitors (in the order they were created - with their children after them)
	 */
	public List<ProgressMonitor> getAllActive() {
		ArrayList<ProgressMonitor> active = new ArrayList<>(children.size() + 1);
		appendActive(active);
		return active;
	}

	/**
	 * Always returns <code>null</code>
	 */
	public ProgressMonitor getParent() {
		return null;
	}

	/**
	 * Always returns an empty list.
	 */
	public List<ProgressMonitor> getContext() {
		return Collections.emptyList();
	}

	public boolean isCancelled() {
		return cancelled.get();
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled.set(cancelled);
		if (cancelled) {
			notifyListeners(this, ProgressUpdateType.CANCELLED);
		}
	}


}
