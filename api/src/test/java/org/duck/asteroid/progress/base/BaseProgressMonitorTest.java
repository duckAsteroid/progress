package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.ProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
		FractionalProgress<Integer> fraction = subject.asInteger(10);
		
		assertEquals(10, (int)fraction.getTotalWork());
		assertEquals(10, (int)fraction.getWorkRemaining());
		assertEquals(0, (int)fraction.getWorkDone());
		assertFalse(subject.isWorkComplete());
		{
			ProgressMonitor subTask = fraction.newSubTask(5, "First Half");
			FractionalProgress<Integer> subFraction = subTask.asInteger(10);

			assertEquals(10, (int)subFraction.getTotalWork());
			assertEquals(10, (int)subFraction.getWorkRemaining());
			assertEquals(0, (int)subFraction.getWorkDone());
			assertFalse(subTask.isWorkComplete());
			// check nothing changed in parent
			assertEquals(10, (int)fraction.getTotalWork());
			assertEquals(10, (int)fraction.getWorkRemaining());
			assertEquals(0, (int)fraction.getWorkDone());
			assertFalse(subject.isWorkComplete());
			
			subFraction.worked(3, "Three tenths");
			// worked 3/10 in subTask
			assertEquals(10, (int)subFraction.getTotalWork());
			assertEquals(7, (int)subFraction.getWorkRemaining());
			assertEquals(3, (int)subFraction.getWorkDone());
			assertFalse(subTask.isWorkComplete());
			// = (3/10) * 0.5 = 0.15 in parent
			assertEquals(0.15, subject.getFractionDone(), 0.0001d);
			// = (3/10) * 5 = 1.5 (or 1)
			assertEquals(10, (int)fraction.getTotalWork());
			assertEquals(9, (int)fraction.getWorkRemaining());
			assertEquals(1, (int)fraction.getWorkDone());
			assertFalse(subject.isWorkComplete());
		}
		
		
		{
			ProgressMonitor secondHalf = fraction.newSubTask(5, "Second Half");
			
			FractionalProgress<Integer> subFraction = secondHalf.asInteger(10);
			
			assertEquals(10, (int)subFraction.getTotalWork());
			assertEquals(10, (int)subFraction.getWorkRemaining());
			assertEquals(0, (int)subFraction.getWorkDone());
			assertFalse(secondHalf.isWorkComplete());

			subFraction.worked(3, "one third");
			// worked 3/10 in subTask
			assertEquals(10, (int)subFraction.getTotalWork());
			assertEquals(7, (int)subFraction.getWorkRemaining());
			assertEquals(3, (int)subFraction.getWorkDone());
			assertFalse(secondHalf.isWorkComplete());
			
			// worked = (3/10) * 5 = 1.5 (1) in parent
			// but workDone = 1; so new workDone = 1
			assertEquals(10, (int)fraction.getTotalWork());
			assertEquals(9, (int)fraction.getWorkRemaining());
			assertEquals(1, (int)fraction.getWorkDone());
			assertFalse(subject.isWorkComplete());

			subFraction.setWorkRemaining(0);
			// worked 10/10 in subTask
			assertEquals(10, (int)subFraction.getTotalWork());
			assertEquals(0, (int)subFraction.getWorkRemaining());
			assertEquals(10, (int)subFraction.getWorkDone());
			assertTrue(secondHalf.isWorkComplete());
			
			// worked = (7/10) * 5 = 3.5 (3) in parent
			// but workDone = 2; so new workDone = 5 
			assertEquals(10, (int)fraction.getTotalWork());
			assertEquals(5, (int)fraction.getWorkRemaining());
			assertEquals(5, (int)fraction.getWorkDone());
			assertFalse(subject.isWorkComplete());
			
		}
	}

	@Test
	public void testSubTaskWithFractionalValues() {
		// a small number of very large subtasks
		FractionalProgress<Integer> fraction = subject.asInteger(2);
		{
			ProgressMonitor subTask = fraction.newSubTask(1, "large subtask 1");
			FractionalProgress<Integer> subfraction = subTask.asInteger(1000);
			for(int i = 1 ; i <= 100; i ++ ) {
				subfraction.worked(10, "Did 10");
				assertEquals(1000, (int)subfraction.getTotalWork());
				assertEquals( i * 10, (int)subfraction.getWorkDone());
				assertEquals(1000 - (i * 10), (int)subfraction.getWorkRemaining());
				assertEquals(i == 100, subfraction.isWorkComplete());
			}
			subfraction.done();
			assertTrue(subfraction.isWorkComplete());
			assertTrue(subTask.isWorkComplete());
			assertFalse(fraction.isWorkComplete());

			assertEquals(0.5d, fraction.getFractionDone(), 0.00001d);
			assertEquals(0.5d, subject.getFractionDone(), 0.00001d);
		}
	}
}
