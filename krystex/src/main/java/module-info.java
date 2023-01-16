module flipkart.krystal.krystex {
  exports com.flipkart.krystal.krystex to
      flipkart.krystal.vajramexecutor.krystex;
  exports com.flipkart.krystal.krystex.node to
      flipkart.krystal.vajramexecutor.krystex;
  exports com.flipkart.krystal.krystex.decorators;
  exports com.flipkart.krystal.krystex.config;

  requires com.google.common;
  requires org.checkerframework.checker.qual;
  requires static io.github.resilience4j.all;
  requires static io.github.resilience4j.bulkhead;
  requires static io.github.resilience4j.circuitbreaker;
  requires static lombok;
  requires static org.slf4j;
  requires flipkart.krystal.common;
}