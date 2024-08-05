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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.testkit.engine.EventConditions.displayName;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.test;
import static org.junitpioneer.internal.PioneerUtils.genericCartesianProduct;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.Event;
import org.junitpioneer.testkit.PioneerTestKit;

class IterableContractTests {

	static final class KnownOrderIterableTests implements StringIterableContract {

		@Override
		public TestIterableGenerator<String> generator() {
			return KnownOrderIterable::new;
		}

		@Override
		public Set<Feature<Iterable<?>>> features() {
			return Set.of(IterableFeature.KNOWN_ORDER);
		}

	}

	private static class KnownOrderIterable implements Iterable<String> {

		private final SampleElements<String> strings;

		public KnownOrderIterable(SampleElements<String> strings) {
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

	static final class UnknownOrderIterableTests implements StringIterableContract {

		@Override
		public TestIterableGenerator<String> generator() {
			return UnknownOrderIterable::new;
		}

	}

	private static class UnknownOrderIterable implements Iterable<String> {

		private final SampleElements<String> strings;

		public UnknownOrderIterable(SampleElements<String> strings) {
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
							return strings.e1();
						case 1:
							return strings.e2();
						case 2:
							return strings.e0();
						default:
							throw new NoSuchElementException();
					}
				}

			};
		}

	}

	@Test
	@DisplayName("IterableContract characteristics")
	@SuppressWarnings("unchecked")
	void iterableContractCharacteristics() {
		// TODO: test Iterator::remove
		// TODO: test Iterable::(forEachRemaining|spliterator)

		int operationSequenceSize = 4;
		var operations = List.of("hasNext()", "next()");
		List<List<String>> allOperationSequences = //
			genericCartesianProduct(nCopies(operationSequenceSize, operations));

		PioneerTestKit
				.executeTestClass(KnownOrderIterableTests.class) //
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

		var knownOrderIterableContract = new KnownOrderIterableTests();
		// TODO: add assertion message
		// TODO: derive from impliesFeatures
		assertEquals(Set.of(IterableFeature.KNOWN_ORDER), knownOrderIterableContract.features());

		var unknownOrderIterableContract = new UnknownOrderIterableTests();
		// TODO: add assertion message
		// TODO: derive from impliesFeatures
		assertEquals(Set.of(IterableFeature.UNKNOWN_ORDER), unknownOrderIterableContract.features());
	}

	private static Condition<Event> conditionForSuccessfulTestOnOperationSequence(List<String> operationSequence) {
		var operationSequenceString = "operation sequence: " + String.join(", ", operationSequence);
		return event(test(), displayName(operationSequenceString), finishedSuccessfully());
	}

}
