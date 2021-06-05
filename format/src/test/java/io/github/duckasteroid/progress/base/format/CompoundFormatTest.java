package io.github.duckasteroid.progress.base.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.github.duckasteroid.progress.base.format.elements.FormatElement;
import io.github.duckasteroid.progress.base.format.elements.Fraction;
import io.github.duckasteroid.progress.base.format.elements.ProgressBar;
import io.github.duckasteroid.progress.base.format.elements.Spinner;
import io.github.duckasteroid.progress.base.format.elements.StaticString;
import io.github.duckasteroid.progress.base.format.elements.TaskName;
import java.util.List;
import org.junit.Test;

public class CompoundFormatTest {
  @Test
  public void testParse() {
    {
      String test = "";
      CompoundFormat cf = CompoundFormat.parse(test);
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