package org.junitpioneer.jupiter.collect;

import java.util.Set;

public interface IterableContract<E> {
  TestIterableGenerator<E> generator();

  SampleElements<E> samples();

  default Set<Feature<Iterable<E>>> features() {
    // TODO
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
