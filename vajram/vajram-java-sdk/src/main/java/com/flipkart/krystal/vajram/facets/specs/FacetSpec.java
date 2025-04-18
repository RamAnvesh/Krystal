package com.flipkart.krystal.vajram.facets.specs;

import com.flipkart.krystal.data.FacetValue;
import com.flipkart.krystal.data.FacetValues;
import com.flipkart.krystal.data.Request;
import com.flipkart.krystal.datatypes.DataType;
import com.flipkart.krystal.facets.Facet;
import com.flipkart.krystal.data.IfNull;
import com.flipkart.krystal.data.IfNull.IfNullThen;
import org.checkerframework.checker.nullness.qual.Nullable;

public sealed interface FacetSpec<T, CV extends Request> extends Facet
    permits AbstractFacetSpec, MandatoryFacetSpec, OptionalFacetSpec {

  boolean isMandatory();

  boolean isBatched();

  boolean canFanout();

  DataType<T> type();

  Class<CV> ofVajram();

  /**
   * Returns the default value for the facet based on its configuration. This is useful in cases
   * where a facet is tagged {@link IfNull} and its {@link IfNullThen#usePlatformDefault()} returns
   * true. The platform must assign a default value to the facet instead of failing. Which default
   * value to use is returned by this method.
   *
   * @throws UnsupportedOperationException if this facet's configuration does not allow using a
   *     platform default value
   * @see DataType#getPlatformDefaultValue()
   */
  Object getPlatformDefaultValue() throws UnsupportedOperationException;

  @Override
  FacetValue<T> getFacetValue(FacetValues facetValues);
}
