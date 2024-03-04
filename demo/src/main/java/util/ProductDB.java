package util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ProductDB {

  ProductDetails getProductDetails(String productId);

  CompletableFuture<ProductDetails> getProductDetailsAsync(String productId);

  Map<String, ProductDetails> getProductDetailsBatch(List<String> productIds);
}
