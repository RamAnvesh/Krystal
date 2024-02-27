package util;

import java.util.concurrent.Callable;
import org.checkerframework.checker.units.qual.C;

public class ScopedValue<T> {

  static <T> ScopedValue<T> newInstance() {
    //noinspection unchecked
    return new ScopedValue<>();
  }

  private ScopedValue() {}

  private ScopedValue(T t) {
    this.t = t;
  }

  private T t;

  public static <T> Carrier where(ScopedValue<T> scopedValue, T value) {
    return new Carrier();
  }

  public T get() {
    return t;
  }

  public static class Carrier {
    public <T> T call(Callable<T> callable) throws Exception {
      return callable.call();
    }
  }
}
