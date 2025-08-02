package com.flipkart.krystal.data;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public record Results<T>(Map<Facets, Errable<T>> values) implements FacetValue<T> {

  private static final Results<?> EMPTY = new Results<>(ImmutableMap.of());

  public static <T> Results<T> empty() {
    //noinspection unchecked
    return (Results<T>) EMPTY;
  }
}
