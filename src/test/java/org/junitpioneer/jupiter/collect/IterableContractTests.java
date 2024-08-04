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

import static org.junit.platform.testkit.engine.EventConditions.dynamicTestRegistered;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.started;
import static org.junit.platform.testkit.engine.EventConditions.test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

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
		// TODO: test Iterator::(hasNext|value|remove) and Iterable::(forEachRemaining|spliterator)
		PioneerTestKit
				.executeTestClass(IterableContractForMinimalIterableTests.class)
				.testEvents()
				.assertEventsMatchLoosely(Stream
						.of(successfulDynamicTestConditions(1, "next(), next(), next(), next()"),
							successfulDynamicTestConditions(2, "next(), next(), hasNext(), next()"),
							successfulDynamicTestConditions(3, "next(), next(), next(), hasNext()"))
						.flatMap(x -> x)
						.toArray(Condition[]::new));
	}

	private static Stream<Condition<Event>> successfulDynamicTestConditions(int number, String operationSequence) {
		var id = String.format("dynamic-test:#%d", number);
		var operationSequenceString = String.format("operation sequence: %s", operationSequence);
		return Stream
				.of(event(dynamicTestRegistered(id)), //
					event(test(id, operationSequenceString), started()), //
					event(test(id, operationSequenceString), finishedSuccessfully()));
	}

}
