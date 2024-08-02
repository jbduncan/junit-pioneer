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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.testkit.PioneerTestKit;

class IterableContractTests {

	static final class IterableContractForMinimalIterableTests implements StringIterableContract {

		@Override
		public TestIterableGenerator<String> generator() {
			return MinimalIterable::new;
		}

	}

	private static class MinimalIterable implements Iterable<String> {

		public MinimalIterable(SampleElements<String> strings) {

		}

		@Override
		public Iterator<String> iterator() {
			return new Iterator<>() {

				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public String next() {
					return "unknown-string";
				}

			};
		}

	}

	@Test
	@DisplayName("IterableContract for MinimalIterable")
	void iterableContractForMinimalIterable() {
		// TODO: test Iterator::(hasNext|value|remove) and Iterable::(forEachRemaining|spliterator)
		PioneerTestKit
				.executeTestClass(IterableContractForMinimalIterableTests.class)
				.testEvents()
				.assertStatistics(stats -> stats.dynamicallyRegistered(1))
				.assertEventsMatchExactly( //
					event(dynamicTestRegistered("dynamic-test:#1")), //
					event(test("dynamic-test:#1", "operation sequence: next(), next(), next(), next()"), started()), //
					event(test("dynamic-test:#1", "operation sequence: next(), next(), next(), next()"),
						finishedSuccessfully()));
	}

}
