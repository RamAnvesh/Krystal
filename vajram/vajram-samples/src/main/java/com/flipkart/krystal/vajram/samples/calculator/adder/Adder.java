package com.flipkart.krystal.vajram.samples.calculator.adder;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.function.Function.identity;

import com.flipkart.krystal.vajram.IOVajram;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.batching.Batch;
import com.flipkart.krystal.vajram.batching.BatchedFacets;
import com.flipkart.krystal.vajram.samples.calculator.adder.AdderFacetUtil.AdderBatchFacets;
import com.flipkart.krystal.vajram.samples.calculator.adder.AdderFacetUtil.AdderCommonFacets;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

@VajramDef
public abstract class Adder extends IOVajram<Integer> {
  public static final LongAdder CALL_COUNTER = new LongAdder();
  public static final String FAIL_ADDER_FLAG = "failAdder";

  @SuppressWarnings("initialization.field.uninitialized")
  static class _Facets {
    @Batch @Input int numberOne;
    @Batch @Input Optional<Integer> numberTwo;

    @Inject
    @Named(FAIL_ADDER_FLAG)
    Optional<Boolean> fail;
  }

  @Output
  static Map<AdderBatchFacets, CompletableFuture<Integer>> add(
      BatchedFacets<AdderBatchFacets, AdderCommonFacets> batchedFacets) {
    CALL_COUNTER.increment();
    if (batchedFacets.commonFacets().fail().orElse(false)) {
      throw new RuntimeException("Adder failed because fail flag was set");
    }
    return batchedFacets.batch().stream()
        .collect(
            toImmutableMap(
                identity(), im -> completedFuture(add(im.numberOne(), im.numberTwo().orElse(0)))));
  }

  public static int add(int a, int b) {
    return a + b;
  }
}
