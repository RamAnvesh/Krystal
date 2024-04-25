package com.flipkart.krystal.krystex.kryon;

import com.flipkart.krystal.krystex.LogicDefinition;
import com.flipkart.krystal.krystex.LogicDefinitionRegistry;
import com.flipkart.krystal.krystex.resolution.CreateNewRequest;
import com.flipkart.krystal.krystex.resolution.FacetsFromRequest;
import com.flipkart.krystal.krystex.resolution.ResolverDefinition;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class KryonDefinitionRegistry {

  private final LogicDefinitionRegistry logicDefinitionRegistry;
  private final Map<KryonId, KryonDefinition> kryonDefinitions = new LinkedHashMap<>();
  private final DependantChainStart dependantChainStart = new DependantChainStart();

  public KryonDefinitionRegistry(LogicDefinitionRegistry logicDefinitionRegistry) {
    this.logicDefinitionRegistry = logicDefinitionRegistry;
  }

  public LogicDefinitionRegistry logicDefinitionRegistry() {
    return logicDefinitionRegistry;
  }

  public KryonDefinition get(KryonId kryonId) {
    KryonDefinition kryon = kryonDefinitions.get(kryonId);
    if (kryon == null) {
      throw new IllegalArgumentException("No Kryon with id %s found".formatted(kryonId));
    }
    return kryon;
  }

  public KryonDefinition newKryonDefinition(
      String kryonId,
      Set<Integer> inputs,
      KryonLogicId outputLogicId,
      LogicDefinition<CreateNewRequest> createNewRequest,
      LogicDefinition<FacetsFromRequest> facetsFromRequest) {
    return newKryonDefinition(
        kryonId, inputs, outputLogicId, ImmutableMap.of(), createNewRequest, facetsFromRequest);
  }

  public KryonDefinition newKryonDefinition(
      String kryonId,
      Set<Integer> inputs,
      KryonLogicId outputLogicId,
      ImmutableMap<Integer, KryonId> dependencyKryons,
      LogicDefinition<CreateNewRequest> createNewRequest,
      LogicDefinition<FacetsFromRequest> facetsFromRequest) {
    return newKryonDefinition(
        kryonId,
        inputs,
        outputLogicId,
        dependencyKryons,
        ImmutableMap.of(),
        createNewRequest,
        facetsFromRequest,
        null);
  }

  public KryonDefinition newKryonDefinition(
      String kryonId,
      Set<Integer> inputs,
      KryonLogicId outputLogicId,
      ImmutableMap<Integer, KryonId> dependencyKryons,
      ImmutableMap</*ResolverID*/ Integer, ResolverDefinition> resolverDefinitions,
      LogicDefinition<CreateNewRequest> createNewRequest,
      LogicDefinition<FacetsFromRequest> facetsFromRequest,
      @Nullable KryonLogicId mulitResolverId) {
    if (!resolverDefinitions.isEmpty() && mulitResolverId == null) {
      throw new IllegalArgumentException("missing multi resolver logic");
    }
    KryonDefinition kryonDefinition =
        new KryonDefinition(
            new KryonId(kryonId),
            inputs,
            outputLogicId,
            dependencyKryons,
            resolverDefinitions,
            Optional.ofNullable(mulitResolverId),
            createNewRequest,
            facetsFromRequest,
            this);
    kryonDefinitions.put(kryonDefinition.kryonId(), kryonDefinition);
    return kryonDefinition;
  }

  public DependantChain getDependantChainsStart() {
    return dependantChainStart;
  }
}
