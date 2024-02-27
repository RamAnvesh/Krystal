package util;

import java.util.concurrent.CompletableFuture;

public record ProductDetails(String productId, CompletableFuture<String> productName) {}
