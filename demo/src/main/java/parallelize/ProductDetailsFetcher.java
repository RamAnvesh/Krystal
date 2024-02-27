package parallelize;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import util.Assisted;
import util.AssistedFactory;
import util.AssitedInject;
import util.ConcurrencyLimit;
import util.ProductCatalogServiceClient;
import util.ProductDetails;

public class ProductDetailsFetcher {

  @AssistedFactory
  public interface ProductDetailsFetcherFactory {
    ProductDetailsFetcher create(List<String> productIds);
  }

  private final List<String> productIds;
  private final ProductCatalogServiceClient svcClient;

  @AssitedInject
  public ProductDetailsFetcher(
      @Assisted List<String> productIds, ProductCatalogServiceClient svcClient) {
    this.productIds = productIds;
    this.svcClient = svcClient;
  }

  @ConcurrencyLimit(30)
  public Map<String, ProductDetails> getProductDetails() {
    CompletableFuture<Integer> productNameLength =
        svcClient
            .getProductDetails("productId")
            .thenApply(ProductDetails::productName)
            .thenCompose(Function.identity())
            .thenApply(String::length);
  }
}
