package io.github.duckasteroid.progress.base.format;

import io.github.duckasteroid.progress.base.format.elements.*;
import org.duck.asteroid.progress.base.format.elements.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CompoundFormatTest {
    @Test
    public void testParse() {
        {
            String test = "";
            CompoundFormat cf  = CompoundFormat.parse(test);
            List<FormatElement> result = cf.elements();
            assertNotNull(result);
            assertEquals(0, result.size());
        }

        {
            String test = "[ %frac% ] %spin:|-% - %prog:=>-22% %name% wibble";
            CompoundFormat cf = CompoundFormat.parse(test);
            List<FormatElement> result = cf.elements();
            assertNotNull(result);
            assertEquals(9, result.size());
            assertTrue(result.get(0) instanceof StaticString);
            assertTrue(result.get(1) instanceof Fraction);
            assertTrue(result.get(2) instanceof StaticString);
            assertTrue(result.get(3) instanceof Spinner);
            assertTrue(result.get(4) instanceof StaticString);
            assertTrue(result.get(5) instanceof ProgressBar);
            assertTrue(result.get(6) instanceof StaticString);
            assertTrue(result.get(7) instanceof TaskName);
            assertTrue(result.get(8) instanceof StaticString);
        }

    }

}