package com.flipkart.krystal.vajram.facets;

import com.flipkart.krystal.vajram.exception.MandatoryFacetMissingException;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;

@UtilityClass
public class FacetValidation {

  public static <T> T validateMandatoryFacet(@Nullable T t, String vajramId, String facetName) {
    if (t == null) {
      throw new MandatoryFacetMissingException(vajramId, facetName);
    }
    return t;
  }
}
