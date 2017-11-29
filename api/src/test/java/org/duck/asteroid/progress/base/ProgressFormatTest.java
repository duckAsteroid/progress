package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.ProgressFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProgressFormatTest {

	ProgressMonitor subject;
	ProgressMonitor subTask;

	@Before
	public void setUp() throws Exception {
		subject = new BaseProgressMonitor();
		subject.setSize(10);
		subTask = subject.newSubTask("SubTask", 4);
		subTask.setSize(100);
		subTask.worked(50, "Half");
	}

	@After
	public void tearDown() throws Exception {
		subTask = null;
		subject = null;
	}

	@Test
	public void testFormat() {
		ProgressFormat f = ProgressFormat.DEFAULT;
		String result = f.format(subTask);
		assertEquals("[0/10] (0%) > SubTask [50/100] (50%) - Half", result);
		result = f.format(subject);
		assertEquals("[0/10] (0%) ", result);
		subTask.done();
		result = f.format(subject);
		assertEquals("[4/10] (40%) - SubTask", result);
	}

}
