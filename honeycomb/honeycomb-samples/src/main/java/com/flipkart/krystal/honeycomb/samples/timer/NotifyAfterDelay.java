package com.flipkart.krystal.honeycomb.samples.timer;

import com.flipkart.krystal.futures.DelayableFuture;
import com.flipkart.krystal.honeycomb.hub.api.HoneycombHubGrpc.HoneycombHubFutureStub;
import com.flipkart.krystal.honeycomb.samples.timer.NotifyAfterDelayFacetUtil.NotifyAfterDelayFacets;
import com.flipkart.krystal.vajram.DelayableVajram;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import jakarta.inject.Inject;
import java.time.Period;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@VajramDef
public abstract class NotifyAfterDelay extends DelayableVajram<Boolean> {
  @SuppressWarnings("initialization.field.uninitialized")
  static class _Facets {
    @Input Period timePeriod;
    @Inject HoneycombHubFutureStub honeycombHub;
  }

  @Output
  static CompletableFuture<Boolean> delay(NotifyAfterDelayFacets facets) {
    CompletableFuture<Boolean> taskSubmission =
        CompletableFuture.supplyAsync(
            () -> {
              return facets.timePeriod() == null;
            },
            ForkJoinPool.commonPool());

    return DelayableFuture.fromTriggerAck(taskSubmission);
  }
}
