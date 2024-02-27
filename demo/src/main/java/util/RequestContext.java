package util;

public record RequestContext() {
  public static final ScopedValue<Long> DEADLINE = ScopedValue.newInstance();
}
