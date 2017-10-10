package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.ProgressMonitor;

import static org.junit.Assert.*;

public class Helper {
	
	public static void testNormalSimpleFlow(ProgressMonitor subject) {
		FractionalProgress<Integer> fraction = subject.asInteger(10);
		FractionAssert<Integer> assertion = FractionAssert.on(fraction);

		assertion.expectedWorkDone(0).check();
		
		for(int i = 1; i < 11 ; i++) {
			fraction.worked(1, "loop " + i);

			assertion.expectedWorkDone(i);
		}
		
		assertion.expectedWorkDone(10);
	}
	
	public static void testFlowUsingSetWorkDone(ProgressMonitor pm) {
		FractionalProgress<Integer> fraction = pm.asInteger(3);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(3, (int)fraction.getWorkRemaining());
		assertEquals(0, (int)fraction.getWorkDone());
		assertFalse(fraction.isWorkComplete());

		fraction.setWorkDone(2);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(1, (int)fraction.getWorkRemaining());
		assertEquals(2, (int)fraction.getWorkDone());
		assertFalse(fraction.isWorkComplete());

		fraction.setWorkDone(1);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(2, (int)fraction.getWorkRemaining());
		assertEquals(1, (int)fraction.getWorkDone());
		assertFalse(fraction.isWorkComplete());

		fraction.setWorkDone(0);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(3, (int)fraction.getWorkRemaining());
		assertEquals(0, (int)fraction.getWorkDone());
		assertFalse(fraction.isWorkComplete());

		fraction.setWorkDone(3);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(0, (int)fraction.getWorkRemaining());
		assertEquals(3, (int)fraction.getWorkDone());
		assertTrue(fraction.isWorkComplete());

		fraction.setWorkDone(5);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(-2, (int)fraction.getWorkRemaining());
		assertEquals(5, (int)fraction.getWorkDone());
		assertTrue(fraction.isWorkComplete());

		fraction.setWorkDone(-5);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(8, (int)fraction.getWorkRemaining());
		assertEquals(-5, (int)fraction.getWorkDone());
		assertFalse(fraction.isWorkComplete());
		
	}
	
	public static void testFlowUsingSetWorkRemaining(ProgressMonitor subject) {
		FractionalProgress<Integer> fraction = subject.asInteger(3);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(3, (int)fraction.getWorkRemaining());
		assertEquals(0, (int)fraction.getWorkDone());
		assertFalse(subject.isWorkComplete());
		
		fraction.setWorkRemaining(1);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(1, (int)fraction.getWorkRemaining());
		assertEquals(2, (int)fraction.getWorkDone());
		assertFalse(subject.isWorkComplete());

		fraction.setWorkRemaining(-5);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(-5, (int)fraction.getWorkRemaining());
		assertEquals(8, (int)fraction.getWorkDone());
		assertTrue(subject.isWorkComplete());

		fraction.setWorkRemaining(3);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(3, (int)fraction.getWorkRemaining());
		assertEquals(0, (int)fraction.getWorkDone());
		assertFalse(subject.isWorkComplete());

		fraction.setWorkRemaining(0);
				
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(0, (int)fraction.getWorkRemaining());
		assertEquals(3, (int)fraction.getWorkDone());
		assertTrue(subject.isWorkComplete());

		fraction.setWorkRemaining(5);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(5, (int)fraction.getWorkRemaining());
		assertEquals(-2, (int)fraction.getWorkDone());
		assertFalse(subject.isWorkComplete());

		fraction.setWorkRemaining(-5);
		
		assertEquals(3, (int)fraction.getTotalWork());
		assertEquals(-5, (int)fraction.getWorkRemaining());
		assertEquals(8, (int)fraction.getWorkDone());
		assertTrue(subject.isWorkComplete());
		
	}
}
