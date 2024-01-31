package com.flipkart.krystal.honeycomb.worker;

import com.flipkart.krystal.data.Inputs;
import com.flipkart.krystal.futures.DelayableFuture;
import com.flipkart.krystal.krystex.KrystalExecutor;
import com.flipkart.krystal.krystex.kryon.KryonExecutionConfig;
import com.flipkart.krystal.krystex.kryon.KryonId;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DelayableKryonExecutor implements KrystalExecutor {

  /**
   * Executes the kryon with the given Id.
   *
   * <p>If the kryon, or any of its pending transitive dependencies is delayable, then a {@link
   * DelayableFuture} is returned. If the kryon and all of its pending transitive dependencies are
   * not delayable, then a {@link CompletableFuture} is returned.
   *
   * @param kryonId
   * @param inputs
   * @param executionConfig
   * @return
   * @param <T>
   */
  @Override
  public <T> CompletableFuture<@Nullable T> executeKryon(
      KryonId kryonId, Inputs inputs, KryonExecutionConfig executionConfig) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void flush() {}

  @Override
  public void close() {}
}
