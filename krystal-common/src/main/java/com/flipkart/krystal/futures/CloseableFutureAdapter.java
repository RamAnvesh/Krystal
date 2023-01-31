package com.flipkart.krystal.futures;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unchecked"})
sealed class CloseableFutureAdapter<T> extends CompletableFuture<T> permits CloseableFuture {

  @Override
  public <U> CloseableFuture<U> thenApply(Function<? super T, ? extends U> fn) {
    return (CloseableFuture<U>) super.thenApply(fn);
  }

  @Override
  public <U> CloseableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
    return (CloseableFuture<U>) super.thenApplyAsync(fn);
  }

  @Override
  public <U> CloseableFuture<U> thenApplyAsync(
      Function<? super T, ? extends U> fn, Executor executor) {
    return (CloseableFuture<U>) super.thenApplyAsync(fn, executor);
  }

  @Override
  public CloseableFuture<Void> thenAccept(Consumer<? super T> action) {
    return (CloseableFuture<Void>) super.thenAccept(action);
  }

  @Override
  public CloseableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
    return (CloseableFuture<Void>) super.thenAcceptAsync(action);
  }

  @Override
  public CloseableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
    return (CloseableFuture<Void>) super.thenAcceptAsync(action, executor);
  }

  @Override
  public CloseableFuture<Void> thenRun(Runnable action) {
    return (CloseableFuture<Void>) super.thenRun(action);
  }

  @Override
  public CloseableFuture<Void> thenRunAsync(Runnable action) {
    return (CloseableFuture<Void>) super.thenRunAsync(action);
  }

  @Override
  public CloseableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
    return (CloseableFuture<Void>) super.thenRunAsync(action, executor);
  }

  @Override
  public <U, V> CloseableFuture<V> thenCombine(
      CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
    return (CloseableFuture<V>) super.thenCombine(other, fn);
  }

  @Override
  public <U, V> CloseableFuture<V> thenCombineAsync(
      CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
    return (CloseableFuture<V>) super.thenCombineAsync(other, fn);
  }

  @Override
  public <U, V> CloseableFuture<V> thenCombineAsync(
      CompletionStage<? extends U> other,
      BiFunction<? super T, ? super U, ? extends V> fn,
      Executor executor) {
    return (CloseableFuture<V>) super.thenCombineAsync(other, fn, executor);
  }

  @Override
  public <U> CloseableFuture<Void> thenAcceptBoth(
      CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
    return (CloseableFuture<Void>) super.thenAcceptBoth(other, action);
  }

  @Override
  public <U> CloseableFuture<Void> thenAcceptBothAsync(
      CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
    return (CloseableFuture<Void>) super.thenAcceptBothAsync(other, action);
  }

  @Override
  public <U> CloseableFuture<Void> thenAcceptBothAsync(
      CompletionStage<? extends U> other,
      BiConsumer<? super T, ? super U> action,
      Executor executor) {
    return (CloseableFuture<Void>) super.thenAcceptBothAsync(other, action, executor);
  }

  @Override
  public CloseableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
    return (CloseableFuture<Void>) super.runAfterBoth(other, action);
  }

  @Override
  public CloseableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
    return (CloseableFuture<Void>) super.runAfterBothAsync(other, action);
  }

  @Override
  public CloseableFuture<Void> runAfterBothAsync(
      CompletionStage<?> other, Runnable action, Executor executor) {
    return (CloseableFuture<Void>) super.runAfterBothAsync(other, action, executor);
  }

  @Override
  public <U> CloseableFuture<U> applyToEither(
      CompletionStage<? extends T> other, Function<? super T, U> fn) {
    return (CloseableFuture<U>) super.applyToEither(other, fn);
  }

  @Override
  public <U> CloseableFuture<U> applyToEitherAsync(
      CompletionStage<? extends T> other, Function<? super T, U> fn) {
    return (CloseableFuture<U>) super.applyToEitherAsync(other, fn);
  }

  @Override
  public <U> CloseableFuture<U> applyToEitherAsync(
      CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
    return (CloseableFuture<U>) super.applyToEitherAsync(other, fn, executor);
  }

  @Override
  public CloseableFuture<Void> acceptEither(
      CompletionStage<? extends T> other, Consumer<? super T> action) {
    return (CloseableFuture<Void>) super.acceptEither(other, action);
  }

  @Override
  public CloseableFuture<Void> acceptEitherAsync(
      CompletionStage<? extends T> other, Consumer<? super T> action) {
    return (CloseableFuture<Void>) super.acceptEitherAsync(other, action);
  }

  @Override
  public CloseableFuture<Void> acceptEitherAsync(
      CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
    return (CloseableFuture<Void>) super.acceptEitherAsync(other, action, executor);
  }

  @Override
  public CloseableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
    return (CloseableFuture<Void>) super.runAfterEither(other, action);
  }

  @Override
  public CloseableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
    return (CloseableFuture<Void>) super.runAfterEitherAsync(other, action);
  }

  @Override
  public CloseableFuture<Void> runAfterEitherAsync(
      CompletionStage<?> other, Runnable action, Executor executor) {
    return (CloseableFuture<Void>) super.runAfterEitherAsync(other, action, executor);
  }

  @Override
  public <U> CloseableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
    return (CloseableFuture<U>) super.thenCompose(fn);
  }

  @Override
  public <U> CloseableFuture<U> thenComposeAsync(
      Function<? super T, ? extends CompletionStage<U>> fn) {
    return (CloseableFuture<U>) super.thenComposeAsync(fn);
  }

  @Override
  public <U> CloseableFuture<U> thenComposeAsync(
      Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
    return (CloseableFuture<U>) super.thenComposeAsync(fn, executor);
  }

  @Override
  public <U> CloseableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
    return (CloseableFuture<U>) super.handle(fn);
  }

  @Override
  public <U> CloseableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
    return (CloseableFuture<U>) super.handleAsync(fn);
  }

  @Override
  public <U> CloseableFuture<U> handleAsync(
      BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
    return (CloseableFuture<U>) super.handleAsync(fn, executor);
  }

  @Override
  public CloseableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
    return (CloseableFuture<T>) super.whenComplete(action);
  }

  @Override
  public CloseableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
    return (CloseableFuture<T>) super.whenCompleteAsync(action);
  }

  @Override
  public CloseableFuture<T> whenCompleteAsync(
      BiConsumer<? super T, ? super Throwable> action, Executor executor) {
    return (CloseableFuture<T>) super.whenCompleteAsync(action, executor);
  }

  @Override
  public CloseableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
    return (CloseableFuture<T>) super.exceptionally(fn);
  }

  @Override
  public CloseableFuture<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) {
    return (CloseableFuture<T>) super.exceptionallyAsync(fn);
  }

  @Override
  public CloseableFuture<T> exceptionallyAsync(
      Function<Throwable, ? extends T> fn, Executor executor) {
    return (CloseableFuture<T>) super.exceptionallyAsync(fn, executor);
  }

  @Override
  public CloseableFuture<T> exceptionallyCompose(
      Function<Throwable, ? extends CompletionStage<T>> fn) {
    return (CloseableFuture<T>) super.exceptionallyCompose(fn);
  }

  @Override
  public CloseableFuture<T> exceptionallyComposeAsync(
      Function<Throwable, ? extends CompletionStage<T>> fn) {
    return (CloseableFuture<T>) super.exceptionallyComposeAsync(fn);
  }

  @Override
  public CloseableFuture<T> exceptionallyComposeAsync(
      Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
    return (CloseableFuture<T>) super.exceptionallyComposeAsync(fn, executor);
  }
}
