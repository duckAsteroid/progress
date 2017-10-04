package org.duck.asteroid.progress.base;

import static org.junit.Assert.*;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.ProgressFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProgressFormatTest {

	ProgressMonitor subject;
	ProgressMonitor subTask;
	private FractionalProgress<Integer> subFrac;

	@Before
	public void setUp() throws Exception {
		subject = new BaseProgressMonitor();
		FractionalProgress<Integer> frac = subject.asInteger(5);
		subTask = frac.newSubTask(2, "SubTask");
		subFrac = subTask.asInteger(100);
		subFrac.worked(50, "Half");
	}

	@After
	public void tearDown() throws Exception {
		subTask = null;
		subject = null;
	}

	@Test
	public void testFormat() {
		ProgressFormat f = ProgressFormat.DEFAULT;
		String result = f.format(subFrac);
		assertEquals("[0.2] (20%) > [1/5] (20%) > SubTask [0.5] (50%) > [50/100] (50%) - Half", result);
		result = f.format(subject);
		assertEquals("[0.2] (20%) ", result);
		subTask.done();
		result = f.format(subject);
		assertEquals("[0.4] (40%) ", result);
	}

}
