package com.flipkart.krystal.vajram;

/**
 * These are vajrams whose output logics delegate computation to an external long-running process
 * which might take an indefinite time (even years) to complete. Such processes generally respond
 * via API callbacks.
 *
 * <p>Call graphs containing {@link DelayableVajram}s need a special runtime which can pause the
 * current execution of the graph and persist all the relevant state. When the long-running process
 * responds via API callback, the runtime is supposed to recieve the call back, reload the paused
 * state and continue the execution of the graph from where it left off.
 *
 * @param <T>
 */
public abstract class DelayableVajram<T> extends IOVajram<T> {}
