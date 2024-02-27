package util;

import java.util.List;
import java.util.Map;

public interface ProductDB {

  ProductDetails getProductDetails(String productId);

  Map<String, ProductDetails> getProductDetailsBatch(List<String> productIds);
}
