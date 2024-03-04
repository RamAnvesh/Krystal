package parallelize;

import static util.RequestContext.DEADLINE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import parallelize.ProductDetailsFetcher.ProductDetailsFetcherFactory;
import util.Assisted;
import util.AssistedFactory;
import util.AssitedInject;
import util.AvailabilityDB;
import util.ConcurrencyLimit;
import util.ProductDetails;
import util.ScopedValue;

public record AvailableProductDetailsFetcher(
    @Assisted List<String> productIds,
    ExecutorService virtualThreadsExecutor,
    ProductDetailsFetcher productDetailsFetcher,
    AvailabilityDB availabilityDB) {

  @AssistedFactory
  public interface AvailableProductDetailsFetcherFactory {
    AvailableProductDetailsFetcher create(List<String> productIds);
  }

  public Map<String, ProductDetails> getProductDetails() {
    double timeRatioForAvailabilityCall = 0.5;
    Future<Map<String, Boolean>> isAvailableFuture =
        virtualThreadsExecutor.submit(
            () ->
                ScopedValue.where(DEADLINE, getNewDeadline(timeRatioForAvailabilityCall))
                    .call(() -> availabilityDB.areProductsAvailable(productIds)));
    Map<String, Boolean> isAvailable = new HashMap<>();
    Map<String, ProductDetails> productDetailsMap = productDetailsFetcher.getProductDetails();
    try {
      isAvailable = isAvailableFuture.get();
    } catch (Exception ignored) {
    }
    Map<String, ProductDetails> result = new HashMap<>();
    for (Entry<String, ProductDetails> e : productDetailsMap.entrySet()) {
      String productId = e.getKey();
      ProductDetails productDetails = e.getValue();
      if (isAvailable.getOrDefault(productId, true)) { // Error-handling
        result.put(productId, productDetails);
      }
    }
    return result;
  }

  private static long getNewDeadline(double ratio) {
    long currentDeadline = DEADLINE.get();
    long currentTime = System.currentTimeMillis();
    long timeRemaining = currentDeadline - currentTime;
    //noinspection NumericCastThatLosesPrecision
    return (long) (currentTime + (timeRemaining * ratio));
  }

  public static <T> T retry(Callable<T> callable, int retryCount) throws Exception {
    Exception exception = null;
    for (int i = 0; i < retryCount; i++) {
      try {
        return callable.call();
      } catch (Exception e) {
        exception = e;
      }
    }
    if (exception != null) {
      throw exception;
    } else {
      throw new AssertionError();
    }
  }
}
