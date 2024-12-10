package com.flipkart.krystal.vajram.das;

import com.flipkart.krystal.vajram.VajramID;
import com.flipkart.krystal.vajram.exec.VajramDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;

public final class VajramIDIndex implements AccessSpecIndex<VajramID> {
  private final Map<VajramID, VajramDefinition> vajrams = new HashMap<>();

  @Override
  public AccessSpecMatchingResult<VajramID> getVajrams(VajramID vajramID) {
    VajramDefinition matchingVajram = vajrams.get(vajramID);
    if (matchingVajram == null) {
      return new AccessSpecMatchingResult<>(
          ImmutableMap.of(), ImmutableMap.of(), ImmutableSet.of(vajramID));
    } else {
      return new AccessSpecMatchingResult<>(
          ImmutableMap.of(vajramID, matchingVajram), ImmutableMap.of(), ImmutableSet.of());
    }
  }

  @Override
  public void add(VajramDefinition vajramDefinition) {
    vajrams.put(vajramDefinition.vajramId(), vajramDefinition);
  }
}
