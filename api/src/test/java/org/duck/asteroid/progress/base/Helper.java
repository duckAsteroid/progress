package org.duck.asteroid.progress.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.duck.asteroid.progress.ProgressMonitor;

public class Helper {
	
	public static void testNormalSimpleFlow(ProgressMonitor subject) {
		subject.begin(10);
		
		assertEquals(10, subject.getTotalWork());
		assertEquals(10, subject.getWorkRemaining());
		assertEquals(0, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		for(int i = 1; i < 11 ; i++) {
				
			subject.worked(1, "loop " + i);
			assertEquals(10, subject.getTotalWork());
			assertEquals(10 - i, subject.getWorkRemaining());
			assertEquals(i, subject.getWorkDone());
		}
		
		assertEquals(10, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(10, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
	}
	
	public static void testFlowUsingSetWorkDone(ProgressMonitor subject) {
		subject.begin(3);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(3, subject.getWorkRemaining());
		assertEquals(0, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkDone(2);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(1, subject.getWorkRemaining());
		assertEquals(2, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkDone(1);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(1, subject.getWorkRemaining());
		assertEquals(2, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkDone(0);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(1, subject.getWorkRemaining());
		assertEquals(2, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkDone(3);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(3, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
		subject.setWorkDone(5);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(3, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
		subject.setWorkDone(-5);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(3, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
	}
	
	public static void testFlowUsingSetWorkRemaining(ProgressMonitor subject) {
		subject.begin(3);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(3, subject.getWorkRemaining());
		assertEquals(0, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkRemaining(1);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(1, subject.getWorkRemaining());
		assertEquals(2, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkRemaining(-5);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(1, subject.getWorkRemaining());
		assertEquals(2, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkRemaining(3);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(1, subject.getWorkRemaining());
		assertEquals(2, subject.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		subject.setWorkRemaining(0);
				
		assertEquals(3, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(3, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
		subject.setWorkRemaining(5);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(3, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
		subject.setWorkRemaining(-5);
		
		assertEquals(3, subject.getTotalWork());
		assertEquals(0, subject.getWorkRemaining());
		assertEquals(3, subject.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
	}
}
