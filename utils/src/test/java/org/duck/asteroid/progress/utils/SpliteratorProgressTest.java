package org.duck.asteroid.progress.utils;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.SimpleProgressFormat;
import org.duck.asteroid.progress.console.ConsoleProgress;

import java.util.*;
import java.util.stream.*;

import static org.junit.Assert.*;

public class SpliteratorProgressTest {

    public static void main(String[] args) {
        final Random rnd = new Random();
        Stream<String> longStream = rnd.longs(500000, 0, 100).mapToObj(Objects::toString);
        Spliterator<String> spliterator = longStream.spliterator();
        ProgressMonitor monitor = ConsoleProgress.createConsoleMonitor(SimpleProgressFormat.DEFAULT, false);
        SpliteratorProgress<String> monitoredSplit = new SpliteratorProgress<>(spliterator, monitor);
        OptionalDouble sum = StreamSupport.stream(monitoredSplit, true).mapToLong(s -> Long.parseLong(s)).average();
        System.out.println(sum.getAsDouble());
    }
}