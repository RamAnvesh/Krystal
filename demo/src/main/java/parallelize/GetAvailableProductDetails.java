package parallelize;

import static parallelize.GetAvailableProductDetailsRequest.isAvailable_n;
import static parallelize.GetAvailableProductDetailsRequest.productDetails_n;

import com.flipkart.krystal.vajram.ComputeVajram;
import com.flipkart.krystal.vajram.Dependency;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import com.flipkart.krystal.vajram.facets.SingleExecute;
import com.flipkart.krystal.vajram.facets.resolution.sdk.Resolve;
import java.util.Optional;
import util.ProductDetails;

@VajramDef
public abstract class GetAvailableProductDetails extends ComputeVajram<ProductDetails> {
  @SuppressWarnings("initialization.field.uninitialized")
  class _Facets {
    @Input String productId;

    @Dependency(onVajram = IsProductAvailable.class)
    Optional<Boolean> isAvailable;

    @Dependency(onVajram = GetProductDetails.class)
    Optional<ProductDetails> productDetails;
  }

  @Resolve(depName = isAvailable_n, depInputs = IsProductAvailableRequest.productId_n)
  static String productIdForIsAvailable(String productId) {
    return productId;
  }

  @Resolve(depName = productDetails_n, depInputs = IsProductAvailableRequest.productId_n)
  static SingleExecute<String> productIdForProductDetails(String productId, boolean isAvailable) {
    if (isAvailable) {
      return SingleExecute.executeWith(productId);
    } else {
      return SingleExecute.skipExecution("Product is not available");
    }
  }

  @Output
  static ProductDetails output(ProductDetails productDetails) {
    return productDetails;
  }
}
