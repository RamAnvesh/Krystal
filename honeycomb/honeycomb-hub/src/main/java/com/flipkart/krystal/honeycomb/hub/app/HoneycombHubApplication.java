package com.flipkart.krystal.honeycomb.hub.app;

import com.flipkart.gjex.core.Application;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.guice.GuiceBundle;
import java.util.Map;

public class HoneycombHubApplication extends Application<HoneycombHubConfig, Map> {

  public static void main(String[] args) throws Exception {
    new HoneycombHubApplication().run(args);
  }

  @Override
  public String getName() {
    return "The Honeycomb-Hub service: Stores and serves the state of asynchronous Krystal graphs";
  }

  @Override
  public void initialize(Bootstrap<HoneycombHubConfig, Map> bootstrap) {
    bootstrap.addBundle(
        new GuiceBundle.Builder<HoneycombHubConfig, Map>()
            .setConfigClass(HoneycombHubConfig.class)
            .addModules(new HoneycombHubModule())
            .build());
  }

  @Override
  public void run(HoneycombHubConfig honeycombHubConfig, Map map, Environment environment) {}
}
