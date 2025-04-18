package com.flipkart.krystal.vajramexecutor.krystex.testharness;

import com.flipkart.krystal.data.Errable;
import com.flipkart.krystal.data.ImmutableFacetValues;
import com.flipkart.krystal.data.ImmutableRequest;
import com.flipkart.krystal.krystex.caching.RequestLevelCache;
import com.flipkart.krystal.krystex.kryon.KryonExecutorConfig.KryonExecutorConfigBuilder;
import com.flipkart.krystal.krystex.kryondecoration.KryonDecoratorConfig;
import com.flipkart.krystal.vajramexecutor.krystex.KrystexVajramExecutorConfig;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Test harness is a collection of software/test data used by developers for unit testing. It is
 * responsible for test drivers and stubs. In the context of Vajrams, VajramTestHarness is
 * responsible for preparing the krystex executor for test by providing the necessary stubbing
 * capability for all the dependency Vajrams of the given dependant Vajram in test.
 */
public class VajramTestHarness {

  private final Map<String, Map<ImmutableFacetValues, Errable<Object>>> vajramIdMockData;
  private final KrystexVajramExecutorConfig kryonExecutorConfigBuilder;
  private final RequestLevelCache requestLevelCache;

  @Inject
  public VajramTestHarness(
      KrystexVajramExecutorConfig krystexVajramExecutorConfig,
      RequestLevelCache requestLevelCache) {
    this.kryonExecutorConfigBuilder = krystexVajramExecutorConfig;
    this.requestLevelCache = requestLevelCache;
    this.vajramIdMockData = new HashMap<>();
  }

  public static VajramTestHarness prepareForTest(
      KrystexVajramExecutorConfig executorConfig, RequestLevelCache requestLevelCache) {
    return new VajramTestHarness(executorConfig, requestLevelCache);
  }

  @SuppressWarnings("unchecked")
  public <T> VajramTestHarness withMock(ImmutableFacetValues request, Errable<T> response) {
    String vajramId = request._vajramID().id();
    Map<ImmutableFacetValues, Errable<Object>> mockDataMap = this.vajramIdMockData.get(vajramId);
    if (Objects.isNull(mockDataMap)) {
      this.vajramIdMockData.put(vajramId, Map.of(request, (Errable<Object>) response));
    } else {
      Errable<Object> errable = mockDataMap.get(request);
      if (Objects.isNull(errable)) {
        mockDataMap.put(request, (Errable<Object>) response);
        this.vajramIdMockData.put(vajramId, mockDataMap);
      }
    }
    return this;
  }

  public KrystexVajramExecutorConfig buildConfig() {
    vajramIdMockData.forEach(
        (s, vajramRequestErrableMap) ->
            vajramRequestErrableMap.forEach(
                (objectVajramRequest, objectErrable) ->
                    requestLevelCache.primeCache(
                        s, objectVajramRequest, objectErrable.toFuture())));
    KryonExecutorConfigBuilder configBuilder =
        kryonExecutorConfigBuilder.kryonExecutorConfigBuilder();
    KryonDecoratorConfig kryonDecoratorConfig =
        configBuilder.build().kryonDecoratorConfigs().get(RequestLevelCache.DECORATOR_TYPE);
    if (kryonDecoratorConfig == null) {
      kryonExecutorConfigBuilder
          .kryonExecutorConfigBuilder()
          .kryonDecoratorConfig(
              RequestLevelCache.DECORATOR_TYPE,
              new KryonDecoratorConfig(
                  RequestLevelCache.DECORATOR_TYPE,
                  executionContext ->
                      vajramIdMockData.containsKey(executionContext.vajramID().id()),
                  executionContext -> RequestLevelCache.DECORATOR_TYPE,
                  kryonExecutionContext -> requestLevelCache));
    }
    return kryonExecutorConfigBuilder;
  }
}
