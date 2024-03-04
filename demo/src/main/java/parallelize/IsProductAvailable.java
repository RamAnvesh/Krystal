package parallelize;

import com.flipkart.krystal.vajram.IOVajram;
import com.flipkart.krystal.vajram.Input;
import com.flipkart.krystal.vajram.Output;
import com.flipkart.krystal.vajram.VajramDef;
import jakarta.inject.Inject;
import java.util.concurrent.CompletableFuture;
import parallelize.IsProductAvailableFacetUtil.IsProductAvailableFacets;
import util.AvailabilityDB;

@VajramDef
abstract class IsProductAvailable extends IOVajram<Boolean> {
  @SuppressWarnings("initialization.field.uninitialized")
  class _Facets {
    @Input String productId;
    @Inject AvailabilityDB availabilityDB;
  }

  @Output
  static CompletableFuture<Boolean> isProductAvailable(
      IsProductAvailableFacets facets) {
    return facets
        .availabilityDB()
        .isProductAvailableAsync(facets.productId());
  }
}
