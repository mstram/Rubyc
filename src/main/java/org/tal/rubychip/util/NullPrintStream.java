package org.tal.rubychip.util;

import java.io.PrintStream;

/**
 *
 * @author Tal Eisenberg 
 */
public class NullPrintStream extends PrintStream {

    public NullPrintStream() {
        super(System.out);
    }

    /**
     * This methods does not print anything.
     */
    public synchronized void write(byte[] b, int off, int len) {
    }

    /**
     * This methods does not print anything.
     */
    public synchronized void write(int b) {
    }

    /**
     * This methods does not print anything.
     */
    private void printLine() {
    }
}
