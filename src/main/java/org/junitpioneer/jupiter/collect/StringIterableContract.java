package org.junitpioneer.jupiter.collect;

public interface StringIterableContract extends IterableContract<String> {
  default SampleElements<String> samples() {
    // TODO
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
