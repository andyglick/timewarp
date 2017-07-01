/*
 * Copyright (c) 2015, Parallel Universe Software Co. All rights reserved.
 * 
 * This program and the accompanying materials are license under the terms of the
 * MIT license.
 */
package co.paralleluniverse.vtime;

/**
 * Sets or gets the virtual clock to be used by the system.
 *
 * @author pron
 */
@SuppressWarnings("WeakerAccess")
public final class VirtualClock {
    private static Clock gClock; // lowest priority note that this isn't volatile
    private static final InheritableThreadLocal<Clock> itlClock = new InheritableThreadLocal<>(); // medium priority
    private static final ThreadLocal<Clock> tlClock = new ThreadLocal<>(); // highest priority

    /**
     * Puts the given clock in effect for the all threads,
     * unless overridden by {@link #setForCurrentThread(Clock) setForCurrentThread}
     * or by {@link #setForCurrentThreadAndChildren(Clock) setForCurrentThreadAndChildren}.
     */
    public static void setGlobal(Clock clock) {
        gClock = clock;
    }

    /**
     * Puts the given clock in effect for the current thread and future child threads,
     * unless overridden by {@link #setForCurrentThread(Clock) setForCurrentThread}.
     */
    public static void setForCurrentThreadAndChildren(Clock clock) {
        itlClock.set(clock);
    }

    /**
     * Puts the given clock in effect for the current thread.
     */
    public static void setForCurrentThread(Clock clock) {
        tlClock.set(clock);
    }

    /**
     * Puts the given clock in effect for future child threads -- <b>but not the current thread</b> --
     * unless overridden by {@link #setForCurrentThread(Clock) setForCurrentThread}.
     */
    @SuppressWarnings("unused")
    public static void setForChildThreads(Clock clock) {
        final Clock current = get();
        setForCurrentThreadAndChildren(clock);
        setForCurrentThread(current);
    }

    /**
     * Puts the given clock in effect for the all threads <b>except the current one</b>,
     * unless overridden by {@link #setForCurrentThread(Clock) setForCurrentThread}
     * or by {@link #setForCurrentThreadAndChildren(Clock) setForCurrentThreadAndChildren}.
     */
    @SuppressWarnings("unused")
    public static void setGlobalExceptCurrentThread(Clock clock) {
        final Clock current = get();
        setGlobal(clock);
        setForCurrentThread(current);
    }

    /**
     * Returns the clock currently in effect for the current thread.
     */
    public static Clock get() {
        Clock clock = tlClock.get();
        if (clock == null)
            clock = itlClock.get();
        if (clock == null)
            clock = gClock;
        if (clock == null)
            clock = SystemClock.instance();
        return clock;
    }

    private VirtualClock() {
    }
}
