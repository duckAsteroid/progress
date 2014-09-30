package org.duck.asteroid.progress.base;

import static org.junit.Assert.*;

import org.duck.asteroid.progress.ProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProgressFormatTest {

	ProgressMonitor subject;
	ProgressMonitor subTask;
	
	@Before
	public void setUp() throws Exception {
		subject = new BaseProgressMonitor();
		subject.begin(5);
		subTask = subject.newSubTask(2, "SubTask");
		subTask.begin(100);
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
		assertEquals("[1/5] (20%) > SubTask [50/100] (50%) - Half", result);
		result = f.format(subject);
		assertEquals("[1/5] (20%) ", result);
		subTask.done();
		result = f.format(subject);
		assertEquals("[2/5] (40%) ", result);
	}

}
