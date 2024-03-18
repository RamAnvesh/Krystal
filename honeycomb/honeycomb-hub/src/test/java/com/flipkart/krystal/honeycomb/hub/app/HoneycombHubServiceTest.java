package com.flipkart.krystal.honeycomb.hub.app;

import static org.junit.jupiter.api.Assertions.*;

import com.flipkart.krystal.honeycomb.hub.api.GetGraphInstanceStateReply;
import com.flipkart.krystal.honeycomb.hub.api.GetGraphInstanceStateRequest;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

class HoneycombHubServiceTest {

  @Test
  void getGraphInstanceState_success() {
    new HoneycombHubService()
        .getGraphInstanceState(
            GetGraphInstanceStateRequest.newBuilder().setGraphInstanceId("abcd123").build(),
            new StreamObserver<GetGraphInstanceStateReply>() {
              @Override
              public void onNext(GetGraphInstanceStateReply value) {}

              @Override
              public void onError(Throwable t) {}

              @Override
              public void onCompleted() {}
            });
  }
}
