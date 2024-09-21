package com.flipkart.krystal.vajram.samples.calculator;

import static com.flipkart.krystal.vajram.facets.resolution.sdk.InputResolvers.dep;
import static com.flipkart.krystal.vajram.facets.resolution.sdk.InputResolvers.depInput;
import static com.flipkart.krystal.vajram.facets.resolution.sdk.InputResolvers.resolve;
import static com.flipkart.krystal.vajram.samples.calculator.Formula2FacetUtil.diff_s;
import static com.flipkart.krystal.vajram.samples.calculator.Formula2FacetUtil.quotient_s;
import static com.flipkart.krystal.vajram.samples.calculator.Formula2Request.a_s;
import static com.flipkart.krystal.vajram.samples.calculator.Formula2Request.p_s;
import static com.flipkart.krystal.vajram.samples.calculator.Formula2Request.q_s;
import static com.flipkart.krystal.vajram.samples.calculator.FormulaRequest.*;
import static com.flipkart.krystal.vajram.samples.calculator.adder.AdderRequest.*;
import static com.flipkart.krystal.vajram.samples.calculator.divider.DividerRequest.*;
import static com.flipkart.krystal.vajram.samples.calculator.subtractor.SubtractorRequest.numberOne_s;
import static com.flipkart.krystal.vajram.samples.calculator.subtractor.SubtractorRequest.numberTwo_s;

import com.flipkart.krystal.vajram.ComputeVajram;
import com.flipkart.krystal.vajram.Dependency;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.facets.resolution.InputResolver;
import com.flipkart.krystal.vajram.samples.calculator.Formula2FacetUtil.Formula2Facets;
import com.flipkart.krystal.vajram.samples.calculator.divider.Divider;
import com.flipkart.krystal.vajram.samples.calculator.subtractor.Subtractor;
import com.google.common.collect.ImmutableCollection;
import java.util.Optional;

/** a/(p-q) */
@VajramDef
public abstract class Formula2 extends ComputeVajram<Integer> {
  @SuppressWarnings("initialization.field.uninitialized")
  static class _Facets {
    @Input int a;
    @Input int p;
    @Input int q;

    @Dependency(onVajram = Subtractor.class)
    int diff;

    @Dependency(onVajram = Divider.class)
    Optional<Integer> quotient;
  }

  @Override
  public ImmutableCollection<InputResolver> getSimpleInputResolvers() {
    return resolve(
        /* diff = subtractor(numberOne=p, numberTwo=q) */
        dep(
            diff_s,
            depInput(numberOne_s).usingAsIs(p_s).asResolver(),
            depInput(numberTwo_s).usingAsIs(q_s).asResolver()),
        /* quotient = divider(numerator = a, denominator= sum) */
        dep(
            quotient_s,
            depInput(numerator_s).usingAsIs(a_s).asResolver(),
            depInput(denominator_s).usingAsIs(diff_s).asResolver()));
  }

  @Output
  static int output(Formula2Facets facets) {
    /* Return quotient */
    return facets
        .quotient()
        .orElseThrow(
            () -> {
              return facets.diff() == 0
                  ? new ArithmeticException("/ by zero")
                  : new IllegalStateException("Did not receive division result");
            });
  }
}
