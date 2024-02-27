package util;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductCatalogServiceClient {

  ProductDetailsBatchResponse getProductDetailsBatch(List<String> productIds);

  CompletableFuture<List<ProductDetails>> getProductDetails(List<String> productIds);

  CompletableFuture<ProductDetails> getProductDetails(String productId);
}
