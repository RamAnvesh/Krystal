package com.flipkart.krystal.vajramexecutor.krystex;

import static com.flipkart.krystal.vajram.VajramID.vajramID;

import com.flipkart.krystal.data.ImmutableRequest;
import com.flipkart.krystal.data.Request;
import com.flipkart.krystal.krystex.KrystalExecutor;
import com.flipkart.krystal.krystex.kryon.KryonExecutionConfig;
import com.flipkart.krystal.krystex.kryon.KryonExecutor;
import com.flipkart.krystal.krystex.kryondecoration.KryonDecoratorConfig;
import com.flipkart.krystal.krystex.kryondecoration.KryonExecutionContext;
import com.flipkart.krystal.vajram.VajramID;
import com.flipkart.krystal.vajram.exec.VajramExecutor;
import com.flipkart.krystal.vajramexecutor.krystex.inputinjection.KryonInputInjector;
import com.flipkart.krystal.vajramexecutor.krystex.inputinjection.VajramInjectionProvider;
import java.util.concurrent.CompletableFuture;
import lombok.Builder;
import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class KrystexVajramExecutor implements VajramExecutor {

  private final VajramKryonGraph vajramKryonGraph;
  private final KrystalExecutor krystalExecutor;

  @Builder
  public KrystexVajramExecutor(
      @NonNull VajramKryonGraph vajramKryonGraph,
      @NonNull KrystexVajramExecutorConfig executorConfig) {
    this.vajramKryonGraph = vajramKryonGraph;
    VajramInjectionProvider inputInjectionProvider = executorConfig.inputInjectionProvider();
    if (inputInjectionProvider != null) {
      executorConfig
          .kryonExecutorConfigBuilder()
          .requestScopedKryonDecoratorConfig(
              KryonInputInjector.DECORATOR_TYPE,
              new KryonDecoratorConfig(
                  KryonInputInjector.DECORATOR_TYPE,
                  /* shouldDecorate= */ executorContext ->
                      isInjectionNeeded(vajramKryonGraph, executorContext),
                  /* instanceIdGenerator= */ executionContext -> KryonInputInjector.DECORATOR_TYPE,
                  /* factory= */ decoratorContext ->
                      new KryonInputInjector(vajramKryonGraph, inputInjectionProvider)));
    }
    this.krystalExecutor =
        new KryonExecutor(
            vajramKryonGraph.kryonDefinitionRegistry(),
            executorConfig.kryonExecutorConfigBuilder().build(),
            executorConfig.requestId());
  }

  private static boolean isInjectionNeeded(
      VajramKryonGraph vajramKryonGraph, KryonExecutionContext executionContext) {
    return vajramKryonGraph
        .getVajramDefinition(vajramID(executionContext.kryonId().value()))
        .map(v -> v.vajramMetadata().isInputInjectionNeeded())
        .orElse(false);
  }

  @Override
  public <T> CompletableFuture<@Nullable T> execute(
      VajramID vajramId, ImmutableRequest<T> vajramRequest) {
    return execute(
        vajramId,
        vajramRequest,
        KryonExecutionConfig.builder().executionId("defaultExecution").build());
  }

  public <T> CompletableFuture<@Nullable T> execute(
      VajramID vajramId, ImmutableRequest<T> vajramRequest, KryonExecutionConfig executionConfig) {
    return executeWithFacets(vajramId, vajramRequest, executionConfig);
  }

  public <T> CompletableFuture<@Nullable T> executeWithFacets(
      VajramID vajramId, Request<T> facets, KryonExecutionConfig executionConfig) {
    return krystalExecutor.executeKryon(
        vajramKryonGraph.getKryonId(vajramId), facets, executionConfig);
  }

  public KrystalExecutor getKrystalExecutor() {
    return krystalExecutor;
  }

  @Override
  public void close() {
    krystalExecutor.close();
  }

  @Override
  public void shutdownNow() {
    krystalExecutor.shutdownNow();
  }
}
