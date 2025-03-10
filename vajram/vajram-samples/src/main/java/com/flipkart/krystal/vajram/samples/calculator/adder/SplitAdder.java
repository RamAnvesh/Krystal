package com.flipkart.krystal.vajram.samples.calculator.adder;

import static com.flipkart.krystal.vajram.facets.One2OneCommand.executeWith;
import static com.flipkart.krystal.vajram.facets.One2OneCommand.skipExecution;
import static com.flipkart.krystal.vajram.samples.calculator.adder.SplitAdder_Fac.splitSum1_i;
import static com.flipkart.krystal.vajram.samples.calculator.adder.SplitAdder_Fac.splitSum2_i;
import static com.flipkart.krystal.vajram.samples.calculator.adder.SplitAdder_Fac.sum_i;
import static com.flipkart.krystal.vajram.samples.calculator.adder.SplitAdder_Req.numbers_i;

import com.flipkart.krystal.annos.ExternalInvocation;
import com.flipkart.krystal.vajram.ComputeVajram;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.facets.Dependency;
import com.flipkart.krystal.vajram.facets.Input;
import com.flipkart.krystal.vajram.facets.Mandatory;
import com.flipkart.krystal.vajram.facets.One2OneCommand;
import com.flipkart.krystal.vajram.facets.Output;
import com.flipkart.krystal.vajram.facets.Using;
import com.flipkart.krystal.vajram.facets.resolution.Resolve;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExternalInvocation(allow = true)
@VajramDef
@SuppressWarnings({"initialization.field.uninitialized", "optional.parameter"})
public abstract class SplitAdder extends ComputeVajram<Integer> {
  static class _Facets {
    @Mandatory @Input List<Integer> numbers;

    @Dependency(onVajram = SplitAdder.class)
    int splitSum1;

    @Dependency(onVajram = SplitAdder.class)
    int splitSum2;

    @Dependency(onVajram = Adder.class)
    int sum;
  }

  @Resolve(dep = splitSum1_i, depInputs = numbers_i)
  public static One2OneCommand<List<Integer>> numbersForSubSplitter1(
      @Using(numbers_i) List<Integer> numbers) {
    if (numbers.size() < 2) {
      return skipExecution(
          "Skipping splitters as count of numbers is less than 2. Will call adder instead");
    } else {
      int subListSize = numbers.size() / 2;
      return executeWith(new ArrayList<>(numbers.subList(0, subListSize)));
    }
  }

  @Resolve(dep = splitSum2_i, depInputs = SplitAdder_Req.numbers_i)
  public static One2OneCommand<List<Integer>> numbersForSubSplitter2(
      @Using(numbers_i) List<Integer> numbers) {
    if (numbers.size() < 2) {
      return skipExecution(
          "Skipping splitters as count of numbers is less than 2. Will call adder instead");
    } else {
      int subListSize = numbers.size() / 2;
      return executeWith(new ArrayList<>(numbers.subList(subListSize, numbers.size())));
    }
  }

  @Resolve(dep = sum_i, depInputs = Adder_Req.numberOne_i)
  public static One2OneCommand<Integer> adderNumberOne(@Using(numbers_i) List<Integer> numbers) {
    if (numbers.size() == 1) {
      return executeWith(numbers.get(0));
    } else if (numbers.isEmpty()) {
      return skipExecution("No numbers provided. Skipping adder call");
    } else {
      return skipExecution("More than 1 numbers provided. SplitAdders will be called instead");
    }
  }

  @Resolve(dep = sum_i, depInputs = Adder_Req.numberTwo_i)
  public static Integer adderNumberTwo(@Using(numbers_i) List<Integer> numbers) {
    return 0;
  }

  @Output
  static Integer add(
      Optional<Integer> splitSum1, Optional<Integer> splitSum2, Optional<Integer> sum) {
    return splitSum1.orElse(0) + splitSum2.orElse(0) + sum.orElse(0);
  }
}
