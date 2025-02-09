package com.flipkart.krystal.data;

/**
 * An Immutable implementation of a Request
 *
 * @param <T> The response type of the vajram corresponding to this request
 */
public interface ImmutableRequest<T> extends Request<T>, ImmutableFacetValuesContainer {

  @Override
  default ImmutableRequest<T> _build() {
    return this;
  }

  @Override
  ImmutableRequest<T> _newCopy();

  /**
   * A builder which can be used to build an {@link ImmutableRequest}
   *
   * @param <T> The response type of the vajram corresponding to this request
   */
  interface Builder<T> extends Request<T>, FacetValuesContainerBuilder {

    @Override
    default Builder<T> _asBuilder() {
      return this;
    }

    @Override
    Builder<T> _newCopy();
  }
}
