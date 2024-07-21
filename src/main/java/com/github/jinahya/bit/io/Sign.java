package com.github.jinahya.bit.io;

/**
 * Constants for sign flags.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
enum Sign {

    /**
     * Constant for <em>unsigned</em> values.
     */
    UNSIGNED,

    /**
     * Constant for <em>signed</em> values.
     */
    SIGNED;

    // -----------------------------------------------------------------------------------------------------------------
    public static Sign valueOf(final boolean unsigned) {
        return unsigned ? UNSIGNED : SIGNED;
    }
}
