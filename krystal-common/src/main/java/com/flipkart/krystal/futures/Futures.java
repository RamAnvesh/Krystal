package com.flipkart.krystal.futures;

import java.util.concurrent.CompletableFuture;

public class Futures {

  public static <T> void setupCompletionPropagation(
      CompletableFuture<? extends T> from, CompletableFuture<T> to) {
    from.whenComplete(
        (result, error) -> {
          if (error != null) {
            to.completeExceptionally(error);
          } else {
            to.complete(result);
          }
        });
  }

  /**
   * Sets up completable Futures such that
   *
   * <ul>
   *   when {@code sourceFuture} completes, its completion is propagated to {@code
   *   destinationFuture}.
   * </ul>
   *
   * <ul>
   *   when {@code destinationFuture} is cancelled, {@code sourceFuture is cancelled}
   * </ul>
   */
  public static <T> void exclusivelyLinkFutures(
      CompletableFuture<? extends T> sourceFuture, CompletableFuture<T> destinationFuture) {
    setupCompletionPropagation(sourceFuture, destinationFuture);
    setupCancellationPropagation(destinationFuture, sourceFuture);
  }

  private static void setupCancellationPropagation(
      CompletableFuture<?> from, CompletableFuture<?> to) {
    from.whenComplete(
        (unused, throwable) -> {
          if (from.isCancelled() && !to.isDone()) {
            to.cancel(true);
          }
        });
  }

  private Futures() {}
}
