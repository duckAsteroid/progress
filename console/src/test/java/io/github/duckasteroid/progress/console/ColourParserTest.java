package io.github.duckasteroid.progress.console;

import io.github.duckasteroid.progress.base.format.CompoundFormat;
import io.github.duckasteroid.progress.base.format.elements.FormatElement;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
public class ColourParserTest {
    @Test
    public void testSetup() {
        CompoundFormat compoundFormat = CompoundFormat.parse("%color:RED%%name%: %color:34% stuff");
        List<FormatElement> elements = compoundFormat.elements();
        assertEquals(3, elements.size());
    }
  
}