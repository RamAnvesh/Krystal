package com.flipkart.krystal.vajram.samples_errors;

import com.flipkart.krystal.annos.ExternalInvocation;
import com.flipkart.krystal.vajram.ComputeVajram;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.samples_errors.TooManyQualifiersFacetUtil.TooManyQualifiersFacets;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ExternalInvocation(allow = true)
@VajramDef
public abstract class TooManyQualifiers extends ComputeVajram<String> {
  @Retention(RetentionPolicy.RUNTIME)
  @Qualifier
  public @interface InjectionQualifier {}

  @SuppressWarnings("initialization.field.uninitialized")
  static class _Facets {
    @Input String input;

    @Inject
    @Named("toInject")
    @InjectionQualifier
    String inject;
  }

  @Output
  static String output(TooManyQualifiersFacets facets) {
    return facets.input() + ' ' + facets.inject();
  }
}
