package io.github.duckasteroid.progress.utils;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.format.SimpleProgressFormat;
import io.github.duckasteroid.progress.console.ConsoleProgress;

import java.util.*;
import java.util.stream.*;

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