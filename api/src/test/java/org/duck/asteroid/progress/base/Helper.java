package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;

public class Helper {
	
	public static void testNormalSimpleFlow(ProgressMonitor subject) {
		subject.setSize(10);
		FractionAssert assertion = FractionAssert.on(subject);

		assertion.expectedWorkDone(0).check();
		
		for(int i = 1; i < 11 ; i++) {
			subject.worked(1, "loop " + i);

			assertion.expectedWorkDone(i);
		}
		
		assertion.expectedWorkDone(10);
	}
}
