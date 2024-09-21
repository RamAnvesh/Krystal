package com.flipkart.krystal.vajram.samples.calculator.divider;

import static com.flipkart.krystal.vajram.VajramID.vajramID;
import static com.flipkart.krystal.vajram.Vajrams.getVajramIdString;
import static com.flipkart.krystal.vajram.samples.Util.javaMethodBenchmark;
import static com.flipkart.krystal.vajram.samples.Util.printStats;
import static com.flipkart.krystal.vajram.samples.calculator.adder.Adder.add;
import static com.flipkart.krystal.vajram.samples.calculator.divider.Divider.divide;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.flipkart.krystal.executors.ThreadPerRequestExecutor;
import com.flipkart.krystal.executors.ThreadPerRequestPool;
import com.flipkart.krystal.krystex.kryon.KryonExecutionConfig;
import com.flipkart.krystal.krystex.kryon.KryonExecutor;
import com.flipkart.krystal.krystex.kryon.KryonExecutorConfig;
import com.flipkart.krystal.krystex.kryon.KryonExecutorMetrics;
import com.flipkart.krystal.utils.LeaseUnavailableException;
import com.flipkart.krystal.vajram.samples.Util;
import com.flipkart.krystal.vajram.samples.calculator.Formula;
import com.flipkart.krystal.vajram.samples.calculator.adder.Adder;
import com.flipkart.krystal.vajramexecutor.krystex.KrystexVajramExecutor;
import com.flipkart.krystal.vajramexecutor.krystex.KrystexVajramExecutorConfig;
import com.flipkart.krystal.vajramexecutor.krystex.VajramKryonGraph;
import com.flipkart.krystal.vajramexecutor.krystex.VajramKryonGraph.Builder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DividerTest {
  private final ThreadPerRequestPool pool =
      new ThreadPerRequestPool("DivderTest", Runtime.getRuntime().availableProcessors());

  private Builder graph;

  @BeforeEach
  void setUp() {
    this.graph = Util.loadFromClasspath(Formula.class.getPackageName());
    Adder.CALL_COUNTER.reset();
  }

  @AfterEach
  void tearDown() {
    pool.close();
  }

  //  @Disabled("Long running benchmark (~16s)")
  @SuppressWarnings("ConstantValue")
  @Test
  void oneExecutor_millionCallsEach_singleCore_benchmark() throws Exception {
    ThreadPerRequestExecutor executorService = getExecutors(1)[0];
    int outerLoopCount = 1;
    int innerLoopCount = 1_000_000;
    int loopCount = outerLoopCount * innerLoopCount;
    VajramKryonGraph graph = this.graph.build();
    long javaNativeTimeNs = javaMethodBenchmark(DividerTest::sync, loopCount);
    long javaFuturesTimeNs = Util.javaFuturesBenchmark(DividerTest::async, loopCount);
    //noinspection unchecked
    CompletableFuture<Integer>[] futures = new CompletableFuture[loopCount];
    KryonExecutorMetrics[] metrics = new KryonExecutorMetrics[outerLoopCount];
    long timeToCreateExecutors = 0;
    long timeToEnqueueVajram = 0;
    long startTime = System.nanoTime();
    for (int outer_i = 0; outer_i < outerLoopCount; outer_i++) {
      long iterStartTime = System.nanoTime();
      try (KrystexVajramExecutor krystexVajramExecutor =
          graph.createExecutor(
              KrystexVajramExecutorConfig.builder()
                  .kryonExecutorConfigBuilder(
                      KryonExecutorConfig.builder()
                          .customExecutorService(Optional.of(executorService)))
                  .requestId("formulaTest")
                  .build())) {
        timeToCreateExecutors += System.nanoTime() - iterStartTime;
        metrics[outer_i] =
            ((KryonExecutor) krystexVajramExecutor.getKrystalExecutor()).getKryonMetrics();
        for (int inner_i = 0; inner_i < innerLoopCount; inner_i++) {
          int iterationNum = outer_i * innerLoopCount + inner_i;
          long enqueueStart = System.nanoTime();
          futures[iterationNum] = executeVajram(krystexVajramExecutor, iterationNum, 10, 5);
          timeToEnqueueVajram += System.nanoTime() - enqueueStart;
        }
      }
    }
    allOf(futures).join();
    long vajramTimeNs = System.nanoTime() - startTime;
    assertThat(
            allOf(futures)
                .whenComplete(
                    (unused, throwable) -> {
                      for (int i = 0, futuresLength = futures.length; i < futuresLength; i++) {
                        CompletableFuture<Integer> future = futures[i];
                        assertThat(future.getNow(0)).isEqualTo((10 + i) / (5 + i));
                      }
                    }))
        .succeedsWithin(ofSeconds(1));
    /*
      Processor: Apple M1 Pro

      Benchmark Results:
        Total java method time: 16,879,000
        Total java futures time: 77,778,625
        Outer Loop Count: 1,000
        Inner Loop Count: 1,000
        Avg. time to Create Executors:12,875 ns
        Avg. time to Enqueue vajrams:1,298 ns
        Avg. time to execute vajrams:19,205 ns
        Throughput executions/s: 52068
        CommandsQueuedCount: 1,002,000
        CommandQueueBypassedCount: 8,000
        Platform overhead over native code: 19,189 ns per request
        Platform overhead over reactive code: 19,128 ns per request
        maxActiveLeasesPerObject: 1, peakAvgActiveLeasesPerObject: 1.0, maxPoolSize: 1
    */
    printStats(
        outerLoopCount,
        innerLoopCount,
        javaNativeTimeNs,
        javaFuturesTimeNs,
        metrics,
        timeToCreateExecutors,
        timeToEnqueueVajram,
        vajramTimeNs,
        pool);
  }

  private static void sync(int value) {
    //noinspection ResultOfMethodCallIgnored
    divide(10 + value, 5 + value);
  }

  private static CompletableFuture<Integer> async(int value) {
    CompletableFuture<Integer> numerator = completedFuture(10 + value);
    CompletableFuture<Integer> denominator = completedFuture(5 + value);
    return allOf(numerator, denominator)
        .thenApply(unused -> divide(numerator.getNow(null), denominator.getNow(null)));
  }

  private static CompletableFuture<Integer> executeVajram(
      KrystexVajramExecutor krystexVajramExecutor, int value, int numerator, int denominator) {
    return krystexVajramExecutor.execute(
        vajramID(getVajramIdString(Divider.class)),
        DividerRequest.builder()
            .numerator(numerator + value)
            .denominator(denominator + value)
            .build(),
        KryonExecutionConfig.builder().executionId("formulaTest" + value).build());
  }

  private ThreadPerRequestExecutor[] getExecutors(int count) throws LeaseUnavailableException {
    ThreadPerRequestExecutor[] singleThreadedExecutors = new ThreadPerRequestExecutor[count];
    for (int i = 0; i < count; i++) {
      singleThreadedExecutors[i] = pool.lease().get();
    }
    return singleThreadedExecutors;
  }
}
