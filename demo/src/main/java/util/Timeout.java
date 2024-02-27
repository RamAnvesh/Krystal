package util;

import java.util.concurrent.TimeUnit;

public @interface Timeout {

  int time();

  TimeUnit units();
}
