package org.junitpioneer.jupiter.collect;

@FunctionalInterface
public interface TestIterableGenerator<E> {
  Iterable<E> create(E[] elements);
}
