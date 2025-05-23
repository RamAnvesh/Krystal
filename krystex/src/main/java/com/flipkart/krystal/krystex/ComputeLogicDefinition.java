package com.flipkart.krystal.krystex;

import com.flipkart.krystal.facets.Facet;
import com.flipkart.krystal.krystex.kryon.KryonLogicId;
import com.flipkart.krystal.tags.ElementTags;
import java.util.Set;

public final class ComputeLogicDefinition<T> extends OutputLogicDefinition<T> {

  public ComputeLogicDefinition(
      KryonLogicId kryonLogicId,
      Set<? extends Facet> usedFacets,
      OutputLogic<T> outputLogic,
      ElementTags tags) {
    super(kryonLogicId, usedFacets, tags, outputLogic);
  }
}
