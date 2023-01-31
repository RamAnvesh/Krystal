package com.flipkart.krystal.futures;

import static com.flipkart.krystal.futures.Futures.setupCompletionPropagation;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A completable future which when 'closed' via the {@link #close(boolean)} method, eventually
 * cancels itself (if it is not yet done) when all its dependants are completed (either normally or
 * exceptionally).
 *
 * <p>This is useful when the future is holding references to costly resources like semaphores,
 * connections and sockets, and it is necessary to release them as early as possible if and when the
 * result from this future is no longer needed (For example, because of the request has timed out)
 */
public final class CloseableFuture<T> extends CloseableFutureAdapter<T> {
  private volatile boolean closed;

  private final Set<CompletableFuture<?>> dependants = ConcurrentHashMap.newKeySet();

  @Override
  public <U> CompletableFuture<U> newIncompleteFuture() {
    if (closed) {
      throw new ClosedFutureException();
    }
    CloseableFuture<U> closeableFuture = new CloseableFuture<>();
    dependants.add(closeableFuture);
    return closeableFuture;
  }

  /**
   * When {@code closeableFuture} is completed, its completion is propagated to {@code dependant}.
   * The {@code dependant} future is added as a dependant to {@code closeableFuture}.
   */
  public void thenComplete(CompletableFuture<? super T> dependant) {
    setupCompletionPropagation(this, dependant);
    dependants.add(dependant);
  }

  public void close(boolean mayInterruptIfRunning) {
    this.closed = true;
    allOf(
            dependants.stream()
                .peek(
                    completableFuture -> {
                      if (completableFuture instanceof CloseableFuture<?> closeableFuture) {
                        closeableFuture.close(mayInterruptIfRunning);
                      }
                    })
                .toArray(CompletableFuture[]::new))
        .whenComplete(
            (unused, throwable) -> {
              if (!this.isDone() && dependants.stream().allMatch(CompletableFuture::isDone)) {
                this.cancel(mayInterruptIfRunning);
              }
            });
  }
}
