package com.flipkart.krystal.honeycomb.hub.app;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;

public class HoneycombHubModule extends AbstractModule {

  @Override
  public void configure() {
    bind(BindableService.class)
        .annotatedWith(Names.named("HoneycombHubService"))
        .to(HoneycombHubService.class);
    bind(BindableService.class)
        .annotatedWith(Names.named("ProtoReflectionService"))
        .toInstance(ProtoReflectionService.newInstance());
  }
}
