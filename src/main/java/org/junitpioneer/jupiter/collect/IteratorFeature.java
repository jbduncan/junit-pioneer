package org.junitpioneer.jupiter.collect;

import java.util.Set;

public enum IteratorFeature implements Feature<Iterable<?>> {
  MODIFIABLE, KNOWN_ORDER;

  @Override
  public Set<Feature<? super Iterable<?>>> impliedFeatures() {
    // TODO
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
