package com.flipkart.krystal.vajram.samples.calculator.add;

import static com.flipkart.krystal.data.IfNull.IfNullThen.FAIL;
import static com.flipkart.krystal.vajram.ComputeDelegationMode.SYNC;

import com.flipkart.krystal.vajram.Trait;
import com.flipkart.krystal.vajram.TraitDef;
import com.flipkart.krystal.vajram.annos.CallGraphDelegationMode;
import com.flipkart.krystal.vajram.facets.Input;
import com.flipkart.krystal.data.IfNull;
import com.google.auto.value.AutoAnnotation;
import jakarta.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/** Adds all the {@code numbers} and returns the result */
@Trait
@CallGraphDelegationMode(SYNC)
public abstract class MultiAdd implements TraitDef<Integer> {
  @SuppressWarnings("initialization.field.uninitialized")
  static class _Inputs {
    @IfNull(FAIL)
    List<Integer> numbers;
  }

  public enum MultiAddType {
    SIMPLE,
    CHAIN,
    SPLIT,
  }

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface MultiAddQualifier {
    MultiAddType value();

    final class Creator {
      public static @AutoAnnotation MultiAddQualifier create(MultiAddType value) {
        return new AutoAnnotation_MultiAdd_MultiAddQualifier_Creator_create(value);
      }

      private Creator() {}
    }
  }
}
