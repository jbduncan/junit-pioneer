/*
 * Copyright 2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter.collect;

import static java.util.Collections.nCopies;
import static org.junit.platform.testkit.engine.EventConditions.displayName;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.test;
import static org.junitpioneer.internal.PioneerUtils.genericCartesianProduct;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.Event;
import org.junitpioneer.testkit.PioneerTestKit;

class IterableContractTests {

	static final class IterableContractForMinimalIterableTests implements StringIterableContract {

		@Override
		public TestIterableGenerator<String> generator() {
			return MinimalIterable::new;
		}

	}

	private static class MinimalIterable implements Iterable<String> {

		private final SampleElements<String> strings;

		public MinimalIterable(SampleElements<String> strings) {
			this.strings = strings;
		}

		@Override
		public Iterator<String> iterator() {
			return new Iterator<>() {

				private int index = 0;

				@Override
				public boolean hasNext() {
					return index < 3;
				}

				@Override
				public String next() {
					switch (index++) {
						case 0:
							return strings.e0();
						case 1:
							return strings.e1();
						case 2:
							return strings.e2();
						default:
							throw new NoSuchElementException();
					}
				}

			};
		}

	}

	@Test
	@DisplayName("IterableContract for MinimalIterable")
	@SuppressWarnings("unchecked")
	void iterableContractForMinimalIterable() {
		// TODO: test Iterator::remove and Iterable::(forEachRemaining|spliterator)

		int operationSequenceSize = 4;
		var operations = List.of("hasNext()", "next()");
		List<List<String>> allOperationSequences = //
			genericCartesianProduct(nCopies(operationSequenceSize, operations));

		PioneerTestKit
				.executeTestClass(IterableContractForMinimalIterableTests.class)
				.testEvents()
				.assertStatistics(stats -> {
					int numTests = 16;
					stats.started(numTests).finished(numTests).succeeded(numTests);
				})
				.assertEventsMatchLoosely( //
					allOperationSequences
							.stream()
							.map(IterableContractTests::conditionForSuccessfulTestOnOperationSequence)
							.toArray(Condition[]::new));
	}

	private static Condition<Event> conditionForSuccessfulTestOnOperationSequence(List<String> operationSequence) {
		var operationSequenceString = "operation sequence: " + String.join(", ", operationSequence);
		return event(test(), displayName(operationSequenceString), finishedSuccessfully());
	}

}
