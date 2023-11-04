package com.flipkart.krystal.caramel.samples.basic;

import com.flipkart.krystal.caramel.model.AccessBeforeInitializationException;
import com.flipkart.krystal.caramel.model.CaramelField;
import com.flipkart.krystal.caramel.model.SimpleCaramelField;
import com.flipkart.krystal.caramel.model.Value;
import com.flipkart.krystal.caramel.model.ValueImpl;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.initialization.qual.NotOnlyInitialized;

// AutoGenerated and managed by Caramel
final class TestPayload implements TestPayloadDefinition {

  TestPayload() {
    x1String = new ValueImpl<>(TestPayloadFields.x1String, this);
    productUpdateEvents = new ValueImpl<>(TestPayloadFields.productUpdateEvents, this);
    initialTransformedProduct = new ValueImpl<>(TestPayloadFields.initialTransformedProduct, this);
    initProductEvent = new ValueImpl<>(TestPayloadFields.initProductEvent, this);
    conditionalTransformedProducts =
        new ValueImpl<>(TestPayloadFields.conditionalTransformedProducts, this);
    triggerUserId = new ValueImpl<>(TestPayloadFields.triggerUserId, this);
    _metrics = new ValueImpl<>(TestPayloadFields.metrics, this);
    metricNames = new ValueImpl<>(TestPayloadFields.metricNames, this);
    isEnableValidation = new ValueImpl<>(TestPayloadFields.isEnableValidation, this);
    string = new ValueImpl<>(TestPayloadFields.string, this);
    secondString = new ValueImpl<>(TestPayloadFields.secondString, this);
    nextProduct = new ValueImpl<>(TestPayloadFields.nextProduct, this);
  }

  interface TestPayloadFields {

    CaramelField<String, TestPayload> x1String =
        new SimpleCaramelField<>(
            "x1String", TestPayload.class, TestPayload::x1String, TestPayload::setX1String);
    CaramelField<ProductUpdateEventsContainer, TestPayload> productUpdateEvents =
        new SimpleCaramelField<>(
            "productUpdateEvents",
            TestPayload.class,
            TestPayload::productUpdateEvents,
            TestPayload::setProductUpdateEvents);
    CaramelField<TransformedProduct, TestPayload> initialTransformedProduct =
        new SimpleCaramelField<>(
            "initialTransformedProduct",
            TestPayload.class,
            TestPayload::initialTransformedProduct,
            TestPayload::setInitialTransformedProduct);
    CaramelField<ProductUpdateEvent, TestPayload> initProductEvent =
        new SimpleCaramelField<>(
            "initProductEvent",
            TestPayload.class,
            TestPayload::initProductEvent,
            TestPayload::setInitProductEvent);
    CaramelField<List<TransformedProduct>, TestPayload> conditionalTransformedProducts =
        new SimpleCaramelField<>(
            "conditionalTransformedProducts",
            TestPayload.class,
            TestPayload::conditionalTransformedProducts,
            TestPayload::setConditionalTransformedProducts);
    CaramelField<String, TestPayload> triggerUserId =
        new SimpleCaramelField<>(
            "triggerUserId",
            TestPayload.class,
            TestPayload::triggerUserId,
            TestPayload::setTriggerUserId);

    CaramelField<Collection<String>, TestPayload> metricNames =
        new SimpleCaramelField<>(
            "metricNames",
            TestPayload.class,
            TestPayload::metricNames,
            TestPayload::setMetricNames);

    CaramelField<Boolean, TestPayload> isEnableValidation =
        new SimpleCaramelField<>(
            "isEnableValidation",
            TestPayload.class,
            TestPayload::isEnableValidation,
            TestPayload::setIsEnableValidation);
    CaramelField<String, TestPayload> string =
        new SimpleCaramelField<>(
            "string", TestPayload.class, TestPayload::string, TestPayload::setString);

    CaramelField<String, TestPayload> secondString =
        new SimpleCaramelField<>(
            "secondString",
            TestPayload.class,
            TestPayload::secondString,
            TestPayload::setSecondString);
    CaramelField<TransformedProduct, TestPayload> nextProduct =
        new SimpleCaramelField<>(
            "nextProduct",
            TestPayload.class,
            TestPayload::nextProduct,
            TestPayload::setNextProduct);
    CaramelField<Collection<Metric>, TestPayload> metrics =
        new SimpleCaramelField<>(
            "metrics", TestPayload.class, TestPayload::metrics, TestPayload::setMetrics);
  }

  @NotOnlyInitialized private final Value<String, TestPayload> x1String;

  @NotOnlyInitialized
  private final Value<ProductUpdateEventsContainer, TestPayload> productUpdateEvents;

  @NotOnlyInitialized
  private final Value<TransformedProduct, TestPayload> initialTransformedProduct;

  @NotOnlyInitialized private final Value<ProductUpdateEvent, TestPayload> initProductEvent;

  @NotOnlyInitialized
  private final Value<List<TransformedProduct>, TestPayload> conditionalTransformedProducts;

  @NotOnlyInitialized private final Value<String, TestPayload> triggerUserId;
  @NotOnlyInitialized private final Value<Collection<Metric>, TestPayload> _metrics;
  @NotOnlyInitialized private final Value<Collection<String>, TestPayload> metricNames;
  @NotOnlyInitialized private final Value<Boolean, TestPayload> isEnableValidation;
  @NotOnlyInitialized private final Value<String, TestPayload> string;
  @NotOnlyInitialized private final Value<String, TestPayload> secondString;
  @NotOnlyInitialized private final Value<TransformedProduct, TestPayload> nextProduct;

  /* ---------- Collection<Metric> metrics - START -----------*/

  @Override
  public Collection<Metric> metrics() {
    return _metrics.getOrThrow();
  }

  public void setMetrics(Collection<Metric> metrics) {
    this._metrics.set(metrics);
  }
  /* ---------- Collection<Metric> metrics - END -----------*/

  public String x1String() {
    return x1String.get().orElseThrow();
  }

  public void setX1String(String string) {
    x1String.set(string);
  }

  @Override
  public ProductUpdateEventsContainer productUpdateEvents() {
    return productUpdateEvents.get().orElseThrow();
  }

  public void setProductUpdateEvents(ProductUpdateEventsContainer productUpdateEvents) {
    this.productUpdateEvents.set(productUpdateEvents);
  }

  @Override
  public TransformedProduct initialTransformedProduct() {
    return initialTransformedProduct.get().orElseThrow();
  }

  public void setInitialTransformedProduct(TransformedProduct transformedProduct) {
    this.initialTransformedProduct.set(transformedProduct);
  }

  @Override
  public ProductUpdateEvent initProductEvent() {
    return initProductEvent.get().orElseThrow();
  }

  public void setInitProductEvent(ProductUpdateEvent productEvent) {
    this.initProductEvent.set(productEvent);
  }

  public List<TransformedProduct> conditionalTransformedProducts() {
    return conditionalTransformedProducts.get().orElseThrow();
  }

  public void setConditionalTransformedProducts(List<TransformedProduct> transformedProducts) {
    this.conditionalTransformedProducts.set(transformedProducts);
  }

  @Override
  public String triggerUserId() {
    return triggerUserId
        .get()
        .orElseThrow(() -> new AccessBeforeInitializationException(triggerUserId));
  }

  public void setTriggerUserId(String triggerUserId) {
    this.triggerUserId.set(triggerUserId);
  }

  @Override
  public Collection<String> metricNames() {
    return metricNames.getOrThrow();
  }

  public void setMetricNames(Collection<String> metrics) {
    this.metricNames.set(metrics);
  }

  @Override
  public boolean isEnableValidation() {
    return isEnableValidation.getOrThrow();
  }

  public void setIsEnableValidation(boolean isEnableValidation) {
    this.isEnableValidation.set(isEnableValidation);
  }

  @Override
  public String string() {
    return string.getOrThrow();
  }

  public void setString(String s) {
    string.set(s);
  }

  @Override
  public String secondString() {
    return secondString.getOrThrow();
  }

  public void setSecondString(String s) {
    secondString.set(s);
  }

  @Override
  public TransformedProduct nextProduct() {
    return nextProduct.getOrThrow();
  }

  public void setNextProduct(TransformedProduct transformedProduct) {
    this.nextProduct.set(transformedProduct);
  }
}