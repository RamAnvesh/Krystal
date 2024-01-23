package com.flipkart.krystal.futures;

import java.util.concurrent.CompletableFuture;

/**
 * A special kind of {@link CompletableFuture} which may not complete anytime soon. i.e. by the time
 * the result is ready, this virtual machine might have been destroyed.
 *
 * <p>This is useful when a computation represents an extremely long-running operation (like batch
 * job) or the result is waiting on a human action (like a manual approaval or an email response).
 * Vajrams which extend this calss are expected to register a remote call-back which will respond to
 * the krystal platform with the result.
 *
 * <p>A Delayable vajram encapsulates another {@link CompletableFuture} which can be accessed via
 * {@link #getDelayableTriggerAck()}. The delayableTriggerAck represents the successful
 * trigger/start of the long-running operation.
 *
 * <p>When a DelayableFuture is encountered, the krystal platform will wait for the {@link
 * #getDelayableTriggerAck()} future to complete, and then may decide to persist the current state
 * of the application so that it can be resumed later when the result is ready. The callback
 * registered above will initiate the resumption.
 *
 * @param <T>
 * @param <A>
 */
public final class DelayableFuture<T, A> extends CompletableFuture<T> {

  private final CompletableFuture<A> delayableTriggerAck;

  private DelayableFuture(CompletableFuture<A> delayableTriggerAck) {
    this.delayableTriggerAck = delayableTriggerAck;
  }

  public static <T, A> DelayableFuture<T, A> fromTriggerAck(
      CompletableFuture<A> delayableTriggerAck) {
    return new DelayableFuture<>(delayableTriggerAck);
  }

  public CompletableFuture<A> getDelayableTriggerAck() {
    return delayableTriggerAck;
  }
}
