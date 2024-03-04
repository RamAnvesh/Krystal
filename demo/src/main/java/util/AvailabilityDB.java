package util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AvailabilityDB {

  boolean isProductAvailable(String productId);

  CompletableFuture<Boolean> isProductAvailableAsync(String productId);

  Map<String, Boolean> areProductsAvailable(List<String> productIds);
}
