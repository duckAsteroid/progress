package io.github.duckasteroid.progress.base.event;

public enum ProgressUpdateType {
    /** A status change */
    STATUS,
    /** Work done (the most common event) */
    WORK,
    /** Progress cancelled */
    CANCELLED,
    /** Progress done */
    DONE;

    /**
     * Parse the string form into an enum
     * @param s the string form
     * @return corresponding enum or null
     * @see #name()
     */
    public static ProgressUpdateType parse( String s) {
        for(ProgressUpdateType type : values()) {
            if (type.name().equals(s)) {
                return type;
            }
        }
        return null;
    }
}
