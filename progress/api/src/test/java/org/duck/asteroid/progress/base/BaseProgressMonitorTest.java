package org.duck.asteroid.progress.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.duck.asteroid.progress.ProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BaseProgressMonitorTest {

	private BaseProgressMonitor subject = null;
	
	@Before
	public void setUp() throws Exception {
		subject = new BaseProgressMonitor();
	}

	@After
	public void tearDown() throws Exception {
		subject = null;
	}

	@Test
	public void testNormalSimpleFlow() {
		Helper.testNormalSimpleFlow(subject);
	}
	
	@Test
	public void testFlowUsingSetWorkDone() {
		Helper.testFlowUsingSetWorkDone(subject);
	}
	
	@Test
	public void testFlowUsingSetWorkRemaining(){
		Helper.testFlowUsingSetWorkRemaining(subject);
	}
	
	@Test
	public void testSubTask() {
		subject.begin(10);
		
		assertEquals(10, subject.getTotalWork());
		assertEquals(10, subject.getWorkRemaining());
		assertEquals(0, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		{
			ProgressMonitor subTask = subject.newSubTask(5, "First Half");
			
			subTask.begin(10);
			
			assertEquals(10, subTask.getTotalWork());
			assertEquals(10, subTask.getWorkRemaining());
			assertEquals(0, subTask.getWorkDone());
			assertFalse(subTask.isWorkComplete());
			// check nothing changed in parent
			assertEquals(10, subject.getTotalWork());
			assertEquals(10, subject.getWorkRemaining());
			assertEquals(0, subject.getWorkDone());
			assertFalse(subject.isWorkComplete());
			
			subTask.worked(3); 
			// worked 3/10 in subTask
			assertEquals(10, subTask.getTotalWork());
			assertEquals(7, subTask.getWorkRemaining());
			assertEquals(3, subTask.getWorkDone());
			assertFalse(subTask.isWorkComplete());
			// = (3/10) * 5 = 1.5 (1) in parent 
			assertEquals(10, subject.getTotalWork());
			assertEquals(9, subject.getWorkRemaining());
			assertEquals(1, subject.getWorkDone());
			assertFalse(subject.isWorkComplete());
		}
		{
			ProgressMonitor secondHalf = subject.newSubTask(5, "Second Half");
			
			secondHalf.begin(10);
			
			assertEquals(10, secondHalf.getTotalWork());
			assertEquals(10, secondHalf.getWorkRemaining());
			assertEquals(0, secondHalf.getWorkDone());
			assertFalse(secondHalf.isWorkComplete());
			
			secondHalf.worked(3); 
			// worked 3/10 in subTask
			assertEquals(10, secondHalf.getTotalWork());
			assertEquals(7, secondHalf.getWorkRemaining());
			assertEquals(3, secondHalf.getWorkDone());
			assertFalse(secondHalf.isWorkComplete());
			
			// worked = (3/10) * 5 = 1.5 (1) in parent
			// but workDone = 1; so new workDone = 2
			assertEquals(10, subject.getTotalWork());
			assertEquals(8, subject.getWorkRemaining());
			assertEquals(2, subject.getWorkDone());
			assertFalse(subject.isWorkComplete());
			
			secondHalf.setWorkRemaining(0);
			// worked 10/10 in subTask
			assertEquals(10, secondHalf.getTotalWork());
			assertEquals(0, secondHalf.getWorkRemaining());
			assertEquals(10, secondHalf.getWorkDone());
			assertTrue(secondHalf.isWorkComplete());
			
			// worked = (7/10) * 5 = 3.5 (3) in parent
			// but workDone = 2; so new workDone = 5 
			assertEquals(10, subject.getTotalWork());
			assertEquals(5, subject.getWorkRemaining());
			assertEquals(5, subject.getWorkDone());
			assertFalse(subject.isWorkComplete());
			
		}
	}
}
