package util;

import java.util.List;
import java.util.Map;

public interface AvailabilityDB {

  boolean isProductAvailable(String productId);

  Map<String, Boolean> areProductsAvailable(List<String> productIds);
}
