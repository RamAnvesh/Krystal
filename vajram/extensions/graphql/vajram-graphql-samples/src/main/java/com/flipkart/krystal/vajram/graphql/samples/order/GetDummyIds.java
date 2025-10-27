package com.flipkart.krystal.vajram.graphql.samples.order;

import static com.flipkart.krystal.model.IfAbsent.IfAbsentThen.FAIL;

import com.flipkart.krystal.model.IfAbsent;
import com.flipkart.krystal.vajram.ComputeVajramDef;
import com.flipkart.krystal.vajram.Vajram;
import com.flipkart.krystal.vajram.facets.Output;
import com.flipkart.krystal.vajram.graphql.api.GraphQLFetcher;
import com.flipkart.krystal.vajram.graphql.samples.dummy.DummyId;
import java.util.List;

@GraphQLFetcher
@Vajram
public abstract class GetDummyIds extends ComputeVajramDef<List<DummyId>> {
  static class _Inputs {
    @IfAbsent(FAIL)
    OrderId id;
  }

  @Output
  static List<DummyId> dummyIds(OrderId id) {
    return List.of(new DummyId(id.value() + "_dummy_1"));
  }
}
