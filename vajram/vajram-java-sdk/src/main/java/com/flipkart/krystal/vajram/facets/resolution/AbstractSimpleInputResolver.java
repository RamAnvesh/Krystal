package com.flipkart.krystal.vajram.facets.resolution;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import com.flipkart.krystal.data.Request;
import com.flipkart.krystal.facets.resolution.ResolutionTarget;
import com.flipkart.krystal.vajram.facets.specs.DependencySpec;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;

/** A resolver which resolves exactly one input of a dependency. */
public abstract sealed class AbstractSimpleInputResolver<
        S, T, CV extends Request, DV extends Request<?>>
    extends AbstractInputResolver implements SimpleInputResolver
    permits SimpleFanoutInputResolver, SimpleOne2OneInputResolver {
  private final DependencySpec<?, CV, DV> dependency;

  private final SimpleInputResolverSpec<T, CV, DV> resolverSpec;

  AbstractSimpleInputResolver(
      DependencySpec<?, CV, DV> dependency,
      SimpleInputResolverSpec<T, CV, DV> resolverSpec,
      boolean canFanout) {
    super(
        resolverSpec.sources(),
        new ResolutionTarget(dependency, resolverSpec.targetInput()),
        canFanout);
    this.dependency = dependency;
    this.resolverSpec = resolverSpec;
  }

  @Override
  public DependencySpec<?, ?, ?> getDependency() {
    return dependency;
  }

  @Override
  public SimpleInputResolverSpec<T, CV, DV> getResolverSpec() {
    return resolverSpec;
  }
}
