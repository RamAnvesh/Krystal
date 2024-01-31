package com.flipkart.krystal.vajram.samples.calculator;

import static com.flipkart.krystal.vajram.facets.resolution.InputResolvers.dep;
import static com.flipkart.krystal.vajram.facets.resolution.InputResolvers.depInput;
import static com.flipkart.krystal.vajram.facets.resolution.InputResolvers.resolve;
import static com.flipkart.krystal.vajram.samples.calculator.FormulaInputUtil.*;
import static com.flipkart.krystal.vajram.samples.calculator.FormulaRequest.*;
import static com.flipkart.krystal.vajram.samples.calculator.adder.AdderRequest.*;
import static com.flipkart.krystal.vajram.samples.calculator.divider.DividerRequest.*;

import com.flipkart.krystal.vajram.ComputeVajram;
import com.flipkart.krystal.vajram.Dependency;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.facets.resolution.InputResolver;
import com.flipkart.krystal.vajram.facets.resolution.Resolve;
import com.flipkart.krystal.vajram.samples.calculator.FormulaInputUtil.FormulaInputs;
import com.flipkart.krystal.vajram.samples.calculator.adder.Adder;
import com.flipkart.krystal.vajram.samples.calculator.adder.AdderRequest;
import com.flipkart.krystal.vajram.samples.calculator.divider.Divider;
import com.flipkart.krystal.vajram.samples.calculator.divider.DividerRequest;
import com.flipkart.krystal.vajram.samples.calculator.multiplier.Multiplier;
import com.flipkart.krystal.vajram.samples.calculator.multiplier.MultiplierRequest;
import com.google.common.collect.ImmutableCollection;

/** a/((p+q)*(2p+q)) */
@VajramDef
@RemotelyInvocable
public abstract class Formula extends ComputeVajram<Integer> {

  @Input int a;
  @Input int p;
  @Input int q;

  @Dependency(onVajram = Adder.class)
  int sum;

  @Dependency(onVajram = Adder.class)
  int sum2;

  @Dependency(onVajram = Multiplier.class)
  int doubling;

  @Dependency(onVajram = Multiplier.class)
  int product;

  @Dependency(onVajram = Divider.class)
  int quotient;

  @Override
  public ImmutableCollection<InputResolver> getSimpleInputResolvers() {
    return resolve(
        dep(
            sum_s,
            depInput(numberOne_s).usingAsIs(p_s).asResolver(),
            depInput(numberTwo_s).usingAsIs(q_s).asResolver()),
        dep(
            doubling_s,
            depInput(MultiplierRequest.numberOne_s).using(() -> 2),
            depInput(MultiplierRequest.numberTwo_s).usingAsIs(p_s).asResolver()),
        dep(
            sum2_s,
            depInput(numberOne_s).usingAsIs(doubling_s).asResolver(),
            depInput(numberTwo_s).usingAsIs(q_s).asResolver()),
        dep(
            product_s,
            depInput(MultiplierRequest.numberOne_s).usingAsIs(sum_s).asResolver(),
            depInput(MultiplierRequest.numberTwo_s).usingAsIs(sum2_s).asResolver()),
        dep(
            quotient_s,
            depInput(numerator_s).usingAsIs(a_s).asResolver(),
            depInput(denominator_s).usingAsIs(product_s).asResolver()));
  }



  @Output
  static int result(FormulaInputs allInputs) {
    /* Return quotient */
    return allInputs.quotient();
  }
}
