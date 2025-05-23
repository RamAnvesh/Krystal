package com.flipkart.krystal.vajramexecutor.krystex;

import com.flipkart.krystal.data.ImmutableRequest;
import com.flipkart.krystal.krystex.KrystalExecutor;
import com.flipkart.krystal.krystex.kryon.KryonExecutionConfig;
import com.flipkart.krystal.krystex.kryon.KryonExecutor;
import com.flipkart.krystal.krystex.kryon.KryonExecutorConfigurator;
import com.flipkart.krystal.krystex.kryondecoration.KryonDecoratorConfig;
import com.flipkart.krystal.krystex.kryondecoration.KryonExecutionContext;
import com.flipkart.krystal.vajram.exec.VajramExecutor;
import com.flipkart.krystal.vajram.inputinjection.VajramInjectionProvider;
import com.flipkart.krystal.vajramexecutor.krystex.inputinjection.KryonInputInjector;
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
    executorConfig
        .kryonExecutorConfigBuilder()
        .configureWith(kryonInputInjector(vajramKryonGraph, executorConfig))
        .configureWith(vajramKryonGraph.inputBatchingConfig());
    this.krystalExecutor =
        new KryonExecutor(
            vajramKryonGraph.kryonDefinitionRegistry(),
            executorConfig.kryonExecutorConfigBuilder().build(),
            executorConfig.requestId());
  }

  @Override
  public <T> CompletableFuture<@Nullable T> execute(ImmutableRequest request) {
    return execute(request, KryonExecutionConfig.builder().executionId("defaultExecution").build());
  }

  public <T> CompletableFuture<@Nullable T> execute(
      ImmutableRequest vajramRequest, KryonExecutionConfig executionConfig) {
    vajramKryonGraph.loadKryonSubGraphIfNeeded(vajramRequest._vajramID());
    return krystalExecutor.executeKryon(vajramRequest, executionConfig);
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

  private static KryonExecutorConfigurator kryonInputInjector(
      VajramKryonGraph vajramKryonGraph, KrystexVajramExecutorConfig executorConfig) {
    VajramInjectionProvider injectionProvider = executorConfig.inputInjectionProvider();
    if (injectionProvider == null) {
      return KryonExecutorConfigurator.NO_OP;
    }
    return configBuilder ->
        configBuilder.kryonDecoratorConfig(
            KryonInputInjector.DECORATOR_TYPE,
            new KryonDecoratorConfig(
                KryonInputInjector.DECORATOR_TYPE,
                /* shouldDecorate= */ executorContext ->
                    isInjectionNeeded(vajramKryonGraph, executorContext),
                /* instanceIdGenerator= */ executionContext -> KryonInputInjector.DECORATOR_TYPE,
                /* factory= */ decoratorContext ->
                    new KryonInputInjector(vajramKryonGraph, injectionProvider)));
  }

  private static boolean isInjectionNeeded(
      VajramKryonGraph vajramKryonGraph, KryonExecutionContext executionContext) {
    return vajramKryonGraph
        .getVajramDefinition(executionContext.vajramID())
        .metadata()
        .isInputInjectionNeeded();
  }
}
