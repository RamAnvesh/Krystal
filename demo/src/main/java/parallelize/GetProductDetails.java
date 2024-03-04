package parallelize;

import com.flipkart.krystal.vajram.IOVajram;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import jakarta.inject.Inject;
import java.util.concurrent.CompletableFuture;
import util.ProductDB;
import util.ProductDetails;

@VajramDef
abstract class GetProductDetails extends IOVajram<ProductDetails> {
  @SuppressWarnings("initialization.field.uninitialized")
  class _Facets {
    @Input String productId;
    @Inject ProductDB productDB;
  }

  @Output
  static CompletableFuture<ProductDetails> output(String productId, ProductDB productDB) {
    return productDB.getProductDetailsAsync(productId);
  }
}
