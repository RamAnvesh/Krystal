package com.flipkart.krystal.config;

import java.util.Map;
import java.util.Optional;

public record MapConfigProvider(Map<String, Object> configs) implements ConfigProvider {

  @Override
  public <T> Optional<T> getConfig(String key) {
    @SuppressWarnings("unchecked")
    Optional<T> t = Optional.ofNullable((T) configs.get(key));
    return t;
  }
}
