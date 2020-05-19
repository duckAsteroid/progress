package org.duck.asteroid.progress.base.format.elements;

import org.duck.asteroid.progress.ProgressMonitor;

public class Colourizer implements FormatElement {
    public enum Color {
        BLACK("30"), RED("31"), GREEN("32"), YELLOW("33"), ;
        private final String code;

        Color(String code) {
            this.code = code;
        }
    }

    public static final String RESET_COLOR = "\033[0m";

    private final String ansiColorCode;

    private final FormatElement wrapped;

    public static Colourizer wrap(FormatElement element, Color c) {
        return new Colourizer(c.code, element);
    }

    public Colourizer(String ansiColorCode, FormatElement wrapped) {
        this.ansiColorCode = ansiColorCode;
        this.wrapped = wrapped;
    }

    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        sb.append("\033[").append(ansiColorCode).append("m");
        wrapped.appendTo(sb, monitor);
        sb.append("\033[0m");
    }
}
