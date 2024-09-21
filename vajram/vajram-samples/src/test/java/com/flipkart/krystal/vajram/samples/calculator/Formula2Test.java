package com.flipkart.krystal.vajram.samples.calculator;

import static com.flipkart.krystal.vajram.VajramID.vajramID;
import static com.flipkart.krystal.vajram.Vajrams.getVajramIdString;
import static com.flipkart.krystal.vajram.samples.Util.javaFuturesBenchmark;
import static com.flipkart.krystal.vajram.samples.Util.javaMethodBenchmark;
import static com.flipkart.krystal.vajram.samples.Util.printStats;
import static com.flipkart.krystal.vajram.samples.calculator.divider.Divider.divide;
import static com.flipkart.krystal.vajram.samples.calculator.subtractor.Subtractor.subtract;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.flipkart.krystal.data.Errable;
import com.flipkart.krystal.executors.ThreadPerRequestExecutor;
import com.flipkart.krystal.executors.ThreadPerRequestPool;
import com.flipkart.krystal.krystex.caching.RequestLevelCache;
import com.flipkart.krystal.krystex.kryon.KryonExecutionConfig;
import com.flipkart.krystal.krystex.kryon.KryonExecutor;
import com.flipkart.krystal.krystex.kryon.KryonExecutor.GraphTraversalStrategy;
import com.flipkart.krystal.krystex.kryon.KryonExecutor.KryonExecStrategy;
import com.flipkart.krystal.krystex.kryon.KryonExecutorConfig;
import com.flipkart.krystal.krystex.kryon.KryonExecutorMetrics;
import com.flipkart.krystal.utils.LeaseUnavailableException;
import com.flipkart.krystal.vajram.VajramID;
import com.flipkart.krystal.vajram.batching.InputBatcherImpl;
import com.flipkart.krystal.vajram.samples.Util;
import com.flipkart.krystal.vajram.samples.calculator.adder.Adder;
import com.flipkart.krystal.vajram.samples.calculator.adder.AdderRequest;
import com.flipkart.krystal.vajram.samples.calculator.divider.DividerRequest;
import com.flipkart.krystal.vajramexecutor.krystex.InputBatcherConfig;
import com.flipkart.krystal.vajramexecutor.krystex.KrystexVajramExecutor;
import com.flipkart.krystal.vajramexecutor.krystex.KrystexVajramExecutorConfig;
import com.flipkart.krystal.vajramexecutor.krystex.VajramKryonGraph;
import com.flipkart.krystal.vajramexecutor.krystex.VajramKryonGraph.Builder;
import com.flipkart.krystal.vajramexecutor.krystex.testharness.VajramTestHarness;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class Formula2Test {

  public static final VajramID FORMULA2_VAJRAM_ID = vajramID(getVajramIdString(Formula2.class));
  private Builder graph;
  private static final String REQUEST_ID = "formulaTest";
  private final RequestLevelCache requestLevelCache = new RequestLevelCache();
  private final ThreadPerRequestPool pool =
      new ThreadPerRequestPool("FormulaTest", Runtime.getRuntime().availableProcessors());

  @BeforeEach
  void setUp() {
    this.graph = Util.loadFromClasspath(Formula2.class.getPackageName());
    Adder.CALL_COUNTER.reset();
  }

  @AfterEach
  void tearDown() {
    pool.close();
  }

  @Test
  void formula2_success() {
    CompletableFuture<Integer> future;
    VajramKryonGraph graph = this.graph.build();
    FormulaRequestContext requestContext = new FormulaRequestContext(100, 25, 5, REQUEST_ID);
    try (KrystexVajramExecutor krystexVajramExecutor =
        graph.createExecutor(KrystexVajramExecutorConfig.builder().requestId(REQUEST_ID).build())) {
      future = executeVajram(krystexVajramExecutor, 0, requestContext);
    }
    //noinspection AssertBetweenInconvertibleTypes https://youtrack.jetbrains.com/issue/IDEA-342354
    assertThat(future).succeedsWithin(1, SECONDS).isEqualTo(5);
  }

  @Disabled("Long running benchmark (~40s)")
  @Test
  void millionExecutors_oneCallEach_singleCoreNoDecorators_benchmark() throws Exception {
    int loopCount = 1_000_000;
    ThreadPerRequestExecutor executorService = getExecutorServices(1)[0];
    VajramKryonGraph graph = this.graph.build();
    long javaNativeTimeNs = javaMethodBenchmark(Formula2Test::syncFormula, loopCount);
    long javaFuturesTimeNs = javaFuturesBenchmark(Formula2Test::asyncFormula, loopCount);
    //noinspection unchecked
    CompletableFuture<Integer>[] futures = new CompletableFuture[loopCount];
    KryonExecutorMetrics[] metrics = new KryonExecutorMetrics[loopCount];
    LongAdder timeToCreateExecutors = new LongAdder();
    LongAdder timeToEnqueueVajram = new LongAdder();
    long startTime = System.nanoTime();
    supplyAsync(
        () -> {
          for (int value = 0; value < loopCount; value++) {
            long iterStartTime = System.nanoTime();
            FormulaRequestContext requestContext =
                new FormulaRequestContext(100, 25, 5, "formulaTest");
            try (KrystexVajramExecutor krystexVajramExecutor =
                graph.createExecutor(
                    KrystexVajramExecutorConfig.builder()
                        .kryonExecutorConfigBuilder(
                            KryonExecutorConfig.builder()
                                .customExecutorService(Optional.of(executorService)))
                        .requestId("formulaTest")
                        .build())) {
              timeToCreateExecutors.add(System.nanoTime() - iterStartTime);
              metrics[value] =
                  ((KryonExecutor) krystexVajramExecutor.getKrystalExecutor())
                      .getKryonMetrics();
              long enqueueStart = System.nanoTime();
              futures[value] = executeVajram(krystexVajramExecutor, value, requestContext);
              timeToEnqueueVajram.add(System.nanoTime() - enqueueStart);
            }
          }
          return null;
        },
        executorService)
        .join();
    allOf(futures).join();
    long vajramTimeNs = System.nanoTime() - startTime;
    assertThat(
        allOf(futures)
            .whenComplete(
                (unused, throwable) -> {
                  for (int i = 0, futuresLength = futures.length; i < futuresLength; i++) {
                    CompletableFuture<Integer> future = futures[i];
                    assertThat(future.getNow(0)).isEqualTo((100 + i) / (25 + i - 5 - i));
                  }
                }))
        .succeedsWithin(ofSeconds(1));

    /*
     * Old Benchmark results (Unable to reproduce :( ) :
     *    loopCount = 1_000_000
     *    maxParallelismPerCore = 10
     *    Processor: 2.6 GHz 6-Core Intel Core i7 (with hyperthreading - 12 virtual cores)
     * Benchmark result:
     *    platform overhead over reactive code = ~13 µs (13,000 ns) per request
     *    maxPoolSize = 120
     *    maxActiveLeasesPerObject: 170
     *    peakAvgActiveLeasesPerObject: 122
     *    Avg. time to Enqueue vajrams : 8,486 ns
     *    Avg. time to execute vajrams : 14,965 ns
     *    Throughput executions/sec: 71000
     */
    /*
     * Processor: Apple M1 Pro
     *
     * Benchmark results;
     *    Total java method time: 6,814,167
     *    Total java futures time: 80,636,209
     *    Outer Loop Count: 1,000,000
     *    Inner Loop Count: 1
     *    Avg. time to Create Executors:444 ns
     *    Avg. time to Enqueue vajrams:1,147 ns
     *    Avg. time to execute vajrams:35,673 ns
     *    Throughput executions/s: 28031
     *    CommandsQueuedCount: 3,000,000
     *    CommandQueueBypassedCount: 8,000,000
     *    Platform overhead over native code: 35,667 ns per request
     *    Platform overhead over reactive code: 35,593 ns per request
     *    maxActiveLeasesPerObject: 1, peakAvgActiveLeasesPerObject: 1.0, maxPoolSize: 1
     */
    printStats(
        loopCount,
        1,
        javaNativeTimeNs,
        javaFuturesTimeNs,
        metrics,
        timeToCreateExecutors.sum(),
        timeToEnqueueVajram.sum(),
        vajramTimeNs,
        pool);
  }

  @Test
  void test() {
    System.out.println(Duration.parse("PT1H").toMillis());
  }

  @Disabled("Long running benchmark (~20s)")
  @Test
  void millionExecutors_oneCallEach_5Cores_benchmark() throws Exception {
    int numberOfCores = 5;
    int loopCount = 1_000_000;
    int executionsPerCore = loopCount / numberOfCores;
    ThreadPerRequestExecutor[] executorServices = getExecutorServices(5);
    VajramKryonGraph graph = this.graph.build();
    long javaNativeTimeNs = javaMethodBenchmark(Formula2Test::syncFormula, loopCount);
    long javaFuturesTimeNs = javaFuturesBenchmark(Formula2Test::asyncFormula, loopCount);
    //noinspection unchecked
    CompletableFuture<Integer>[] futures = new CompletableFuture[loopCount];
    KryonExecutorMetrics[] metrics = new KryonExecutorMetrics[loopCount];
    LongAdder timeToCreateExecutors = new LongAdder();
    LongAdder timeToEnqueueVajram = new LongAdder();
    long startTime = System.nanoTime();
    //noinspection unchecked
    CompletableFuture<Void>[] coreSubmissions = new CompletableFuture[numberOfCores];
    for (int currentCore = 0; currentCore < numberOfCores; currentCore++) {
      var executorService = executorServices[currentCore];
      int start = currentCore * executionsPerCore;
      int finish = start + executionsPerCore;
      FormulaRequestContext requestContext = new FormulaRequestContext(100, 25, 5, "formulaTest");
      coreSubmissions[currentCore] =
          supplyAsync(
              () -> {
                for (int value = start; value < finish; value++) {
                  long iterStartTime = System.nanoTime();
                  try (KrystexVajramExecutor krystexVajramExecutor =
                      graph.createExecutor(
                          KrystexVajramExecutorConfig.builder()
                              .kryonExecutorConfigBuilder(
                                  KryonExecutorConfig.builder()
                                      .customExecutorService(Optional.of(executorService)))
                              .requestId("formulaTest")
                              .build())) {
                    timeToCreateExecutors.add(System.nanoTime() - iterStartTime);
                    metrics[value] =
                        ((KryonExecutor) krystexVajramExecutor.getKrystalExecutor())
                            .getKryonMetrics();
                    long enqueueStart = System.nanoTime();
                    futures[value] = executeVajram(krystexVajramExecutor, value, requestContext);
                    timeToEnqueueVajram.add(System.nanoTime() - enqueueStart);
                  }
                }
                return null;
              },
              executorService);
    }
    // Wait for submissions to all cores to finish
    allOf(coreSubmissions).join();
    // Wait for all 1 million computations to finish
    allOf(futures).join();
    long vajramTimeNs = System.nanoTime() - startTime;
    assertThat(
        allOf(futures)
            .whenComplete(
                (unused, throwable) -> {
                  for (int i = 0, futuresLength = futures.length; i < futuresLength; i++) {
                    CompletableFuture<Integer> future = futures[i];
                    assertThat(future.getNow(0)).isEqualTo((100 + i) / (25 + i - 5 - i));
                  }
                }))
        .succeedsWithin(ofSeconds(1));

    /*
        Processor: Apple M1 Pro

        Total java method time: 23,330,000 ns
        Total java futures time: 98,646,584 ns
        Outer Loop Count: 1,000,000
        Inner Loop Count: 1
        Avg. time to Create Executors:2,909 ns
        Avg. time to Enqueue vajrams:4,013 ns
        Avg. time to execute vajrams:36,311 ns
        Throughput executions/s: 27539
        CommandsQueuedCount: 3,000,000
        CommandQueueBypassedCount: 8,000,000
        Platform overhead over native code: 36,288 ns per request
        Platform overhead over reactive code: 36,213 ns per request
        maxActiveLeasesPerObject: 1, peakAvgActiveLeasesPerObject: 1.0, maxPoolSize: 5
    */
    printStats(
        loopCount,
        1,
        javaNativeTimeNs,
        javaFuturesTimeNs,
        metrics,
        timeToCreateExecutors.sum(),
        timeToEnqueueVajram.sum(),
        vajramTimeNs,
        pool);
  }

  //  @Disabled("Long running benchmark (~16s)")
  @Test
  void oneLakhExecutors_10CallsEach_singleCore_benchmark() throws Exception {
    ThreadPerRequestExecutor executorService = getExecutorServices(1)[0];
    int outerLoopCount = 100_000;
    int innerLoopCount = 10;
    int loopCount = outerLoopCount * innerLoopCount;
    VajramKryonGraph graph = this.graph.build();
    long javaNativeTimeNs = javaMethodBenchmark(Formula2Test::syncFormula, loopCount);
    long javaFuturesTimeNs = javaFuturesBenchmark(Formula2Test::asyncFormula, loopCount);
    //noinspection unchecked
    CompletableFuture<Integer>[] futures = new CompletableFuture[loopCount];
    KryonExecutorMetrics[] metrics = new KryonExecutorMetrics[outerLoopCount];
    long timeToCreateExecutors = 0;
    long timeToEnqueueVajram = 0;
    long startTime = System.nanoTime();
    for (int outer_i = 0; outer_i < outerLoopCount; outer_i++) {
      long iterStartTime = System.nanoTime();
      FormulaRequestContext requestContext = new FormulaRequestContext(100, 25, 5, "formulaTest");
      try (KrystexVajramExecutor krystexVajramExecutor =
          graph.createExecutor(
              KrystexVajramExecutorConfig.builder()
                  .kryonExecutorConfigBuilder(
                      KryonExecutorConfig.builder()
                          .customExecutorService(Optional.of(executorService)))
                  .requestId("formula2Test")
                  .build())) {
        timeToCreateExecutors += System.nanoTime() - iterStartTime;
        metrics[outer_i] =
            ((KryonExecutor) krystexVajramExecutor.getKrystalExecutor()).getKryonMetrics();
        for (int inner_i = 0; inner_i < innerLoopCount; inner_i++) {
          int iterationNum = outer_i * innerLoopCount + inner_i;
          long enqueueStart = System.nanoTime();
          futures[iterationNum] =
              executeVajram(krystexVajramExecutor, iterationNum, requestContext);
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
                    assertThat(future.getNow(0)).isEqualTo((100 + i) / (25 + i - 5 - i));
                  }
                }))
        .succeedsWithin(ofSeconds(1));
    /*
        Total java method time: 23,224,000
        Total java futures time: 122,600,708
        Outer Loop Count: 100,000
        Inner Loop Count: 10
        Avg. time to Create Executors:778 ns
        Avg. time to Enqueue vajrams:1,302 ns
        Avg. time to execute vajrams:19,254 ns
        Throughput executions/s: 51934
        CommandsQueuedCount: 1,200,000
        CommandQueueBypassedCount: 800,000
        Platform overhead over native code: 19,232 ns per request
        Platform overhead over reactive code: 19,132 ns per request
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

  @Disabled("Long running benchmark (~16s)")
  @Test
  void thousandExecutors_1000CallsEach_singleCore_benchmark() throws Exception {
    ThreadPerRequestExecutor executorService = getExecutorServices(1)[0];
    int outerLoopCount = 1000;
    int innerLoopCount = 1000;
    int loopCount = outerLoopCount * innerLoopCount;
    VajramKryonGraph graph = this.graph.build();
    long javaNativeTimeNs = javaMethodBenchmark(Formula2Test::syncFormula, loopCount);
    long javaFuturesTimeNs = javaFuturesBenchmark(Formula2Test::asyncFormula, loopCount);
    //noinspection unchecked
    CompletableFuture<Integer>[] futures = new CompletableFuture[loopCount];
    KryonExecutorMetrics[] metrics = new KryonExecutorMetrics[outerLoopCount];
    long timeToCreateExecutors = 0;
    long timeToEnqueueVajram = 0;
    long startTime = System.nanoTime();
    for (int outer_i = 0; outer_i < outerLoopCount; outer_i++) {
      long iterStartTime = System.nanoTime();
      FormulaRequestContext requestContext = new FormulaRequestContext(100, 20, 5, "formulaTest");
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
          futures[iterationNum] =
              executeVajram(krystexVajramExecutor, iterationNum, requestContext);
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
                    assertThat(future.getNow(0)).isEqualTo((100 + i) / (20 + i + 5 + i));
                  }
                }))
        .succeedsWithin(ofSeconds(1));
    assertThat(Adder.CALL_COUNTER.sum()).isEqualTo(outerLoopCount);
    /*
       Old code performance:
       Total java method time: 29,883,631
       Total java futures time: 66,435,359
       Loop Count: 1,000,000
       Avg. time to Create Executors:14,522 ns
       Avg. time to Enqueue vajrams:2,904 ns
       Avg. time to execute vajrams:190,280 ns
       Throughput executions/s: 5263
       CommandsQueuedCount: 1,003,999
       CommandQueueBypassedCount: 9,998,637
       Platform overhead over native code: 190,251 ns per request
       Platform overhead over reactive code: 190,214 ns per request
       maxActiveLeasesPerObject: 165, peakAvgActiveLeasesPerObject: 164.66666666666666, maxPoolSize: 12
    */
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

  //  @Disabled("Long running benchmark (~16s)")
  @SuppressWarnings("ConstantValue")
  @Test
  void oneExecutor_millionCallsEach_singleCore_benchmark() throws Exception {
    ThreadPerRequestExecutor executorService = getExecutorServices(1)[0];
    int outerLoopCount = 1;
    int innerLoopCount = 1_000_000;
    int loopCount = outerLoopCount * innerLoopCount;
    VajramKryonGraph graph = this.graph.build();
    long javaNativeTimeNs = javaMethodBenchmark(Formula2Test::syncFormula, loopCount);
    long javaFuturesTimeNs = javaFuturesBenchmark(Formula2Test::asyncFormula, loopCount);
    //noinspection unchecked
    CompletableFuture<Integer>[] futures = new CompletableFuture[loopCount];
    KryonExecutorMetrics[] metrics = new KryonExecutorMetrics[outerLoopCount];
    long timeToCreateExecutors = 0;
    long timeToEnqueueVajram = 0;
    long startTime = System.nanoTime();
    for (int outer_i = 0; outer_i < outerLoopCount; outer_i++) {
      long iterStartTime = System.nanoTime();
      FormulaRequestContext requestContext = new FormulaRequestContext(100, 20, 5, "formulaTest");
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
          futures[iterationNum] =
              executeVajram(krystexVajramExecutor, iterationNum, requestContext);
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
                    assertThat(future.getNow(0)).isEqualTo((100 + i) / (20 + i + 5 + i));
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

  private static CompletableFuture<Integer> executeVajram(
      KrystexVajramExecutor krystexVajramExecutor, int value, FormulaRequestContext rc) {
    return krystexVajramExecutor.execute(
        FORMULA2_VAJRAM_ID,
        Formula2Request.builder().a(rc.a + value).p(rc.p + value).q(rc.q + value).build(),
        KryonExecutionConfig.builder().executionId("formula2Test" + value).build());
  }

  private static void syncFormula(Integer value) {
    //noinspection ResultOfMethodCallIgnored
    divide(value, subtract(25, 5));
  }

  private static CompletableFuture<Integer> asyncFormula(int value) {
    CompletableFuture<Integer> numerator = completedFuture(value);
    CompletableFuture<Integer> add1 = completedFuture(25);
    CompletableFuture<Integer> add2 = completedFuture(5);
    CompletableFuture<Integer> sum =
        allOf(add1, add2).thenApply(unused -> subtract(add1.getNow(null), add2.getNow(null)));
    return allOf(numerator, sum)
        .thenApply(unused -> divide(numerator.getNow(null), sum.getNow(null)));
  }

  private record FormulaRequestContext(int a, int p, int q, String requestId) {}

  @Test
  void formula_success_withAllMockedDependencies() throws Exception {
    CompletableFuture<Integer> future;
    VajramKryonGraph graph = this.graph.build();
    graph.registerInputBatchers(
        vajramID(getVajramIdString(Adder.class)),
        InputBatcherConfig.simple(() -> new InputBatcherImpl<>(100)));
    KrystexVajramExecutorConfig executorConfigBuilder =
        KrystexVajramExecutorConfig.builder()
            .requestId(REQUEST_ID)
            .kryonExecutorConfigBuilder(
                KryonExecutorConfig.builder()
                    .kryonExecStrategy(KryonExecStrategy.BATCH)
                    .graphTraversalStrategy(GraphTraversalStrategy.DEPTH))
            .build();
    FormulaRequestContext requestContext = new FormulaRequestContext(100, 20, 5, REQUEST_ID);
    try (KrystexVajramExecutor krystexVajramExecutor =
        graph.createExecutor(
            VajramTestHarness.prepareForTest(executorConfigBuilder, requestLevelCache)
                .withMock(
                    AdderRequest.builder().numberOne(20).numberTwo(5).build(),
                    Errable.withValue(25))
                .withMock(
                    DividerRequest.builder().numerator(100).denominator(25).build(),
                    Errable.withValue(4))
                .buildConfig())) {
      future = executeVajram(krystexVajramExecutor, 0, requestContext);
    }
    assertThat(future.get()).isEqualTo(4);
    assertThat(Adder.CALL_COUNTER.sum()).isEqualTo(0);
  }

  @Test
  void formula_success_with_mockedDependencyAdder() throws Exception {
    CompletableFuture<Integer> future;
    VajramKryonGraph graph = this.graph.build();
    graph.registerInputBatchers(
        vajramID(getVajramIdString(Adder.class)),
        InputBatcherConfig.simple(() -> new InputBatcherImpl<>(100)));
    KrystexVajramExecutorConfig kryonExecutorConfigBuilder =
        KrystexVajramExecutorConfig.builder()
            .requestId(REQUEST_ID)
            .kryonExecutorConfigBuilder(
                KryonExecutorConfig.builder()
                    .kryonExecStrategy(KryonExecStrategy.BATCH)
                    .graphTraversalStrategy(GraphTraversalStrategy.DEPTH))
            .build();
    FormulaRequestContext requestContext = new FormulaRequestContext(100, 20, 5, REQUEST_ID);
    try (KrystexVajramExecutor krystexVajramExecutor =
        graph.createExecutor(
            VajramTestHarness.prepareForTest(kryonExecutorConfigBuilder, requestLevelCache)
                .withMock(
                    AdderRequest.builder().numberOne(20).numberTwo(5).build(),
                    Errable.withValue(25))
                .buildConfig())) {
      future = executeVajram(krystexVajramExecutor, 0, requestContext);
    }
    assertThat(future.get()).isEqualTo(4);
    assertThat(Adder.CALL_COUNTER.sum()).isEqualTo(0);
  }

  @Test
  void formula_success_with_mockedDependencyDivider() throws Exception {
    CompletableFuture<Integer> future;
    VajramKryonGraph graph = this.graph.build();
    graph.registerInputBatchers(
        vajramID(getVajramIdString(Adder.class)),
        InputBatcherConfig.simple(() -> new InputBatcherImpl<>(100)));
    KrystexVajramExecutorConfig executorConfig =
        KrystexVajramExecutorConfig.builder()
            .requestId(REQUEST_ID)
            .kryonExecutorConfigBuilder(
                KryonExecutorConfig.builder()
                    .kryonExecStrategy(KryonExecStrategy.BATCH)
                    .graphTraversalStrategy(GraphTraversalStrategy.DEPTH))
            .build();
    FormulaRequestContext requestContext = new FormulaRequestContext(100, 20, 5, REQUEST_ID);
    try (KrystexVajramExecutor krystexVajramExecutor =
        graph.createExecutor(
            VajramTestHarness.prepareForTest(executorConfig, requestLevelCache)
                .withMock(
                    DividerRequest.builder().numerator(100).denominator(25).build(),
                    Errable.withValue(4))
                .buildConfig())) {
      future = executeVajram(krystexVajramExecutor, 0, requestContext);
    }
    assertThat(future.get()).isEqualTo(4);
    assertThat(Adder.CALL_COUNTER.sum()).isEqualTo(1);
  }

  @Test
  void formula_failure() {
    CompletableFuture<Integer> future;
    VajramKryonGraph graph = this.graph.build();
    graph.registerInputBatchers(
        vajramID(getVajramIdString(Adder.class)),
        InputBatcherConfig.simple(() -> new InputBatcherImpl<>(100)));
    KrystexVajramExecutorConfig kryonExecutorConfigBuilder =
        KrystexVajramExecutorConfig.builder()
            .requestId(REQUEST_ID)
            .kryonExecutorConfigBuilder(
                KryonExecutorConfig.builder()
                    .kryonExecStrategy(KryonExecStrategy.BATCH)
                    .graphTraversalStrategy(GraphTraversalStrategy.DEPTH))
            .build();
    FormulaRequestContext requestContext = new FormulaRequestContext(100, 0, 0, REQUEST_ID);
    try (KrystexVajramExecutor krystexVajramExecutor =
        graph.createExecutor(
            VajramTestHarness.prepareForTest(kryonExecutorConfigBuilder, requestLevelCache)
                .withMock(
                    AdderRequest.builder().numberOne(0).numberTwo(0).build(), Errable.withValue(0))
                .buildConfig())) {
      future = executeVajram(krystexVajramExecutor, 0, requestContext);
    }
    assertThat(future)
        .failsWithin(ofSeconds(1))
        .withThrowableOfType(ExecutionException.class)
        .withCauseInstanceOf(ArithmeticException.class)
        .withMessage("java.lang.ArithmeticException: / by zero");
  }

  private ThreadPerRequestExecutor[] getExecutorServices(int count)
      throws LeaseUnavailableException {
    ThreadPerRequestExecutor[] singleThreadedExecutors = new ThreadPerRequestExecutor[count];
    for (int i = 0; i < count; i++) {
      singleThreadedExecutors[i] = pool.lease().get();
    }
    return singleThreadedExecutors;
  }
}
