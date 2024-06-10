package org.junitpioneer.jupiter.collect;

import java.util.Set;

@FunctionalInterface
public interface Feature<T> {
  Set<Feature<? super T>> impliedFeatures();
}
