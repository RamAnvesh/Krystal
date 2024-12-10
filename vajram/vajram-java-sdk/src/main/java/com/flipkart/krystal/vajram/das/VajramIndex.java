package com.flipkart.krystal.vajram.das;

import com.flipkart.krystal.vajram.VajramID;
import com.flipkart.krystal.vajram.exec.VajramDefinition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An index of vajrams which supports their retrieval by an access spec in sublinear time complexity
 * wrt. to the number of vajrams in the index.
 */
public final class VajramIndex {
  private static final Set<Class<? extends DataAccessSpec>> SUPPORTED_ACCESS_SPECS =
      Set.of(VajramID.class, GraphQl.class);
  private final Map<Class<? extends DataAccessSpec>, AccessSpecIndex<? extends DataAccessSpec>>
      accessSpecIndices = new HashMap<>();

  public VajramIndex() {
    SUPPORTED_ACCESS_SPECS.forEach(
        aClass -> {
          if (VajramID.class.equals(aClass)) {
            accessSpecIndices.put(VajramID.class, new VajramIDIndex());
          } else if (GraphQl.class.equals(aClass)) {
            accessSpecIndices.put(GraphQl.class, new GraphQlIndex());
          }
        });
  }

  public <T extends DataAccessSpec> AccessSpecMatchingResult<T> getVajrams(T accessSpec) {
    @SuppressWarnings("unchecked")
    AccessSpecIndex<T> accessSpecIndex =
        (AccessSpecIndex<T>) accessSpecIndices.get(accessSpec.getClass());
    return Optional.ofNullable(accessSpecIndex)
        .map(index -> index.getVajrams(accessSpec))
        .orElse(
            new AccessSpecMatchingResult<>(
                ImmutableMap.of(), ImmutableMap.of(), ImmutableList.of(accessSpec)));
  }

  public void add(VajramDefinition vajramDefinition) {
    accessSpecIndices.values().forEach(accessSpecIndex -> accessSpecIndex.add(vajramDefinition));
  }
}
