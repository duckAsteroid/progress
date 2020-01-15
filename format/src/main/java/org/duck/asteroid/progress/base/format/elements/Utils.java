package org.duck.asteroid.progress.base.format.elements;

/**
 * Utilities used by classes in this package
 */
class Utils {
    // need to remove all line feed chars and carriage return from status message
    public static final String sanitize(String s) {
        // FIXME Keep the regex as a static?
        return s.replaceAll("[\f\r]","");
    }


}
