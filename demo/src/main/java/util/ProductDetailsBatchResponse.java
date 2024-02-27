package util;

import java.util.List;
import java.util.Set;

public record ProductDetailsBatchResponse(List<ProductDetails> found, Set<String> notFound) {}
