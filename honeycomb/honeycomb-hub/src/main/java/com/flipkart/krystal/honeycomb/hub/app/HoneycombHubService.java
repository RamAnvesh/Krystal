package com.flipkart.krystal.honeycomb.hub.app;

import com.flipkart.gjex.core.service.Api;
import com.flipkart.krystal.honeycomb.hub.api.FacetInfo;
import com.flipkart.krystal.honeycomb.hub.api.FacetValueRef;
import com.flipkart.krystal.honeycomb.hub.api.GetAvailableFacetsReply;
import com.flipkart.krystal.honeycomb.hub.api.GetAvailableFacetsRequest;
import com.flipkart.krystal.honeycomb.hub.api.GetGraphInstanceStateReply;
import com.flipkart.krystal.honeycomb.hub.api.GetGraphInstanceStateRequest;
import com.flipkart.krystal.honeycomb.hub.api.HoneycombHubGrpc.HoneycombHubImplBase;
import com.flipkart.krystal.honeycomb.hub.api.KryonExecInfo;
import com.flipkart.krystal.honeycomb.hub.api.ValueRef;
import com.flipkart.krystal.honeycomb.hub.api.ValueRef.ValueRefType;
import com.google.protobuf.Any;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HoneycombHubService extends HoneycombHubImplBase {

  @Override
  @Api
  public void getGraphInstanceState(
      GetGraphInstanceStateRequest request,
      StreamObserver<GetGraphInstanceStateReply> responseObserver) {
    log.error("Received call to getGraphInstanceState");
    log.error(request.getGraphInstanceId());
    GetGraphInstanceStateReply reply =
        GetGraphInstanceStateReply.newBuilder()
            .setGraphInstanceId(request.getGraphInstanceId())
            .addKryonExecInfos(
                KryonExecInfo.newBuilder()
                    .addFacetInfos(
                        FacetInfo.newBuilder()
                            .setFacetName("f1")
                            .setFacetValue(
                                FacetValueRef.newBuilder()
                                    .setValueRef(
                                        ValueRef.newBuilder()
                                            .setRefType(ValueRefType.INLINE)
                                            .setValue(Any.pack(StringValue.of("facet value")))
                                            .build())
                                    .build())
                            .build())
                    .build())
            .build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

  @Override
  public void getAvailableFacets(
      GetAvailableFacetsRequest request, StreamObserver<GetAvailableFacetsReply> responseObserver) {
    super.getAvailableFacets(request, responseObserver);
  }
}
