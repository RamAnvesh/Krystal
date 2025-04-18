package com.flipkart.krystal.vajram.samples.calculator.add;

import static com.flipkart.krystal.data.IfNull.IfNullThen.DEFAULT_TO_ZERO;
import static com.flipkart.krystal.data.IfNull.IfNullThen.FAIL;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.function.Function.identity;

import com.flipkart.krystal.data.IfNull;
import com.flipkart.krystal.vajram.IOVajramDef;
import com.flipkart.krystal.vajram.Vajram;
import com.flipkart.krystal.vajram.batching.Batched;
import com.flipkart.krystal.vajram.batching.BatchesGroupedBy;
import com.flipkart.krystal.vajram.facets.Input;
import com.flipkart.krystal.vajram.facets.Output;
import com.google.common.collect.ImmutableCollection;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

/**
 * Adds two numbers - {@code numberOne} and {@code numberTwo} and returns the result. While {@code
 * numberOne} is a mandatory input, {@code numberTwo} is optional and defaults to zero if not set.
 */
@Vajram
@SuppressWarnings({"initialization.field.uninitialized", "optional.parameter"})
public abstract class Add extends IOVajramDef<Integer> {

  static class _Inputs {
    /** The first number to add */
    @IfNull(FAIL)
    @Batched
    int numberOne;

    /** The second number to add */
    @IfNull(DEFAULT_TO_ZERO)
    @Batched
    int numberTwo;
  }

  static class _InternalFacets {
    /**
     * Flag to indicate if the adder should fail. If set to true, the adder will throw a {@link
     * RuntimeException}
     */
    @BatchesGroupedBy
    @Named(FAIL_ADDER_FLAG)
    @Inject
    boolean fail;
  }

  public static final LongAdder CALL_COUNTER = new LongAdder();

  public static final String FAIL_ADDER_FLAG = "failAdder";

  @Output
  static Map<Add_BatchItem, CompletableFuture<Integer>> add(
      ImmutableCollection<Add_BatchItem> _batchItems, Optional<Boolean> fail) {
    CALL_COUNTER.increment();
    if (fail.orElse(false)) {
      throw new RuntimeException("Adder failed because fail flag was set");
    }
    return _batchItems.stream()
        .collect(
            toImmutableMap(
                identity(), batch -> completedFuture(add(batch.numberOne(), batch.numberTwo()))));
  }

  public static int add(int a, int b) {
    return a + b;
  }
}
