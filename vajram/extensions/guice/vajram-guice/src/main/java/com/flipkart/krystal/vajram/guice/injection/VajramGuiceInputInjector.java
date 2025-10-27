package com.flipkart.krystal.vajram.guice.injection;

import static com.flipkart.krystal.data.Errable.errableFrom;
import static com.flipkart.krystal.data.Errable.nil;
import static com.flipkart.krystal.facets.FacetType.INJECTION;

import com.flipkart.krystal.core.VajramID;
import com.flipkart.krystal.data.Errable;
import com.flipkart.krystal.except.StackTracelessException;
import com.flipkart.krystal.vajram.facets.specs.FacetSpec;
import com.flipkart.krystal.vajram.inputinjection.VajramInjectionProvider;
import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VajramGuiceInputInjector implements VajramInjectionProvider {

  private final Injector injector;
  private final Map<VajramID, Map<String, Provider<?>>> providerCache = new LinkedHashMap<>();

  @Inject
  public VajramGuiceInputInjector(Injector injector) {
    this.injector = injector;
  }

  @Override
  public <T> Errable<@NonNull T> get(VajramID vajramID, FacetSpec<T, ?> facetDef) {
    if (!INJECTION.equals(facetDef.facetType())) {
      return nil();
    }
    return errableFrom(
        () -> {
          @SuppressWarnings("unchecked")
          Provider<T> provider =
              (Provider<T>)
                  providerCache
                      .computeIfAbsent(vajramID, _v -> new LinkedHashMap<>())
                      .computeIfAbsent(
                          facetDef.name(),
                          _i -> {
                            try {
                              Type type = facetDef.type().javaReflectType();
                              var annotation = getQualifier(vajramID, facetDef);
                              if (annotation.isEmpty()) {
                                return injector.getProvider(Key.get(type));
                              } else {
                                return injector.getProvider(Key.get(type, annotation.get()));
                              }
                            } catch (ClassNotFoundException e) {
                              throw new StackTracelessException(
                                  "Unable to load data type of Input", e);
                            }
                          });
          return provider.get();
        });
  }

  private <T> Optional<Annotation> getQualifier(VajramID vajramID, FacetSpec<T, ?> facetDef) {
    List<Annotation> qualifierAnnotations =
        facetDef.tags().annotations().stream()
            .<Annotation>mapMulti(
                (tag, consumer) -> {
                  boolean isQualifierAnno =
                      tag.annotationType().getAnnotation(Qualifier.class) != null;
                  if (isQualifierAnno) {
                    consumer.accept(tag);
                  }
                })
            .toList();
    if (qualifierAnnotations.isEmpty()) {
      return Optional.empty();
    } else if (qualifierAnnotations.size() == 1) {
      return Optional.ofNullable(qualifierAnnotations.get(0));
    } else {
      throw new IllegalStateException(
          ("More than one @jakarta.inject.Qualifier annotations (%s) found on input '%s' of vajram '%s'."
                  + " This is not allowed")
              .formatted(qualifierAnnotations, facetDef.name(), vajramID.id()));
    }
  }
}
