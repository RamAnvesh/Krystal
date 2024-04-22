package com.flipkart.krystal.vajram.samples.calculator.adder;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.function.Function.identity;

import com.flipkart.krystal.vajram.IOVajram;
import com.flipkart.krystal.vajram.facets.Input;
import com.flipkart.krystal.vajram.facets.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.batching.Batch;
import com.flipkart.krystal.vajram.batching.BatchedFacets;
import com.flipkart.krystal.vajram.samples.calculator.adder.AdderFacetUtil.AdderBatchFacets;
import com.flipkart.krystal.vajram.samples.calculator.adder.AdderFacetUtil.AdderCommonFacets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

@VajramDef
public abstract class Adder extends IOVajram<Integer> {
  public static final LongAdder CALL_COUNTER = new LongAdder();

  @SuppressWarnings("initialization.field.uninitialized")
  static class _Facets {
    @Batch @Input int numberOne;
    @Batch @Input Optional<Integer> numberTwo;
  }

  @Output
  static Map<AdderBatchFacets, CompletableFuture<Integer>> add(
      BatchedFacets<AdderBatchFacets, AdderCommonFacets> batchedFacets) {
    CALL_COUNTER.increment();
    return batchedFacets.batch().stream()
        .collect(
            toImmutableMap(
                identity(),
                batch -> completedFuture(add(batch.numberOne(), batch.numberTwo().orElse(0)))));
  }

  public static int add(int a, int b) {
    return a + b;
  }
}
