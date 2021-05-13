package org.duck.asteroid.progress.console;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.elements.FormatElement;

import java.util.Optional;

/**
 * Wraps an underlying format element and adds ANSI colours
 */
public class Colourizer implements FormatElement.Wrapping {
    public enum Color {
        BLACK("30"), RED("31"), GREEN("32"), YELLOW("33"), ;
        private final String code;

        Color(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Optional<Color> parse(String s) {
            for(Color c : values()) {
                if (c.name().equals(s)) {
                    return Optional.of(c);
                }
            }
            return Optional.empty();
        }
    }

    public static final String RESET_COLOR = "\033[0m";

    private final String ansiColorCode;

    private FormatElement wrapped;

    public static Colourizer wrap(FormatElement element, Color c) {
        return new Colourizer(c.code, element);
    }

    public Colourizer(String ansiColorCode, FormatElement wrapped) {
        this.ansiColorCode = ansiColorCode;
        this.wrapped = wrapped;
    }

    public Colourizer(String ansiColorCode) {
        this.ansiColorCode = ansiColorCode;
    }

    @Override
    public void setWrapped(FormatElement wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isWrapping() {
        return wrapped != null;
    }

    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        if (wrapped != null) {
            sb.append("\033[").append(ansiColorCode).append("m");
            wrapped.appendTo(sb, monitor);
            sb.append(RESET_COLOR);
        }
    }
}
