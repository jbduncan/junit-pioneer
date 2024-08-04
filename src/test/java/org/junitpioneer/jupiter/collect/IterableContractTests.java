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
	void iterableContractForMinimalIterable() {
		// TODO: test Iterator::(hasNext|value|remove) and Iterable::(forEachRemaining|spliterator)
		PioneerTestKit
				.executeTestClass(IterableContractForMinimalIterableTests.class)
				.testEvents()
				.assertEventsMatchLoosely( //
					event(dynamicTestRegistered("dynamic-test:#1")), //
					event(test("dynamic-test:#1", "operation sequence: next(), next(), next(), next()"), started()), //
					event(test("dynamic-test:#1", "operation sequence: next(), next(), next(), next()"),
						finishedSuccessfully()), //
					event(dynamicTestRegistered("dynamic-test:#2")), //
					event(test("dynamic-test:#2", "operation sequence: next(), next(), hasNext(), next()"), started()), //
					event(test("dynamic-test:#2", "operation sequence: next(), next(), hasNext(), next()"),
						finishedSuccessfully()), //
					event(dynamicTestRegistered("dynamic-test:#3")), //
					event(test("dynamic-test:#3", "operation sequence: next(), next(), next(), hasNext()"), started()), //
					event(test("dynamic-test:#3", "operation sequence: next(), next(), next(), hasNext()"),
						finishedSuccessfully()));
	}

}
