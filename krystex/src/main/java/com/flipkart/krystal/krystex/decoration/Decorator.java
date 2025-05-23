package com.flipkart.krystal.krystex.decoration;

import com.flipkart.krystal.krystex.dependencydecoration.DependencyDecorator;
import com.flipkart.krystal.krystex.kryondecoration.KryonDecorator;
import com.flipkart.krystal.krystex.logicdecoration.LogicDecorator;

public sealed interface Decorator permits DependencyDecorator, KryonDecorator, LogicDecorator {

  /**
   * The identifier for this object which is used to prevent duplicate kryon decorators decorating
   * the same kryon. This means one kryon can never be decorated by two Decorators whose return
   * value from this method is the same.
   *
   * @implNote By default, this method returns the class name of this LogicDecorator.
   *     Implementations can override this method for more customized LogicDecorator deduplication
   *     strategies.
   * @return the id of this decorator.
   */
  default String decoratorType() {
    return this.getClass().getName();
  }

  default void executeCommand(DecoratorCommand decoratorCommand) {}
}
