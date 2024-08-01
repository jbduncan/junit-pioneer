package org.junitpioneer.jupiter.collect;

import org.junit.jupiter.api.Test;
import org.junitpioneer.testkit.PioneerTestKit;

import java.util.List;

class IterableContractTests {
  private static final class ExampleIterableContractTestCase implements StringIterableContract {
    @Override
    public TestIterableGenerator<String> generator() {
      return List::of;
    }
  }

  @Test
  void exampleIterableContract() {
    PioneerTestKit.executeTestClass(ExampleIterableContractTestCase.class)
        .allEvents()
        .assertStatistics(eventStatistics -> {
          eventStatistics.dynamicallyRegistered(1);
        });
  }
}