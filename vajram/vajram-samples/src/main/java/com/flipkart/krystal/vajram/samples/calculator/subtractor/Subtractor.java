package com.flipkart.krystal.vajram.samples.calculator.subtractor;

import com.flipkart.krystal.vajram.ComputeVajram;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.samples.calculator.subtractor.SubtractorFacetUtil.SubtractorFacets;
import java.util.Optional;

@VajramDef
@SuppressWarnings("initialization.field.uninitialized")
public abstract class Subtractor extends ComputeVajram<Integer> {
  static class _Facets {
    @Input int numberOne;
    @Input Optional<Integer> numberTwo;
  }

  @Output
  static int subtract(SubtractorFacets allInputs) {
    int numberOne = allInputs.numberOne();
    int numberTwo = allInputs.numberTwo().orElse(0);
    return subtract(numberOne, numberTwo);
  }

  public static int subtract(int numberOne, int numberTwo) {
    return numberOne - numberTwo;
  }
}
