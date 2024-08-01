package org.junitpioneer.jupiter.collect;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.Set;
import java.util.stream.Stream;

public interface IterableContract<E> {
  TestIterableGenerator<E> generator();

  SampleElements<E> samples();

  default Set<Feature<Iterable<E>>> features() {
    // TODO
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @TestFactory
  default Stream<DynamicNode> iterable() {
    // TODO
    return Stream.of();
  }
}
