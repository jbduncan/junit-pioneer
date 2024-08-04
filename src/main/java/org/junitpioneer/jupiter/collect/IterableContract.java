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

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junitpioneer.jupiter.collect.IteratorOperation.HAS_NEXT;
import static org.junitpioneer.jupiter.collect.IteratorOperation.NEXT;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public interface IterableContract<E> {

	TestIterableGenerator<E> generator();

	SampleElements<E> samples();

	default Set<Feature<Iterable<E>>> features() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@TestFactory
	default Stream<DynamicNode> iterableTests() {

		// TODO: do all 4-element sequences comprising of NEXT and/or HAS_NEXT, i.e.:
		//   - NEXT, NEXT, NEXT, NEXT
		//   - NEXT, NEXT, NEXT, HAS_NEXT
		//   - NEXT, NEXT, HAS_NEXT, NEXT
		//   - ...

		// TODO: Give each dynamic test a URI Test Source:
		//  https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests-uri-test-source

		return Stream.of(dynamicTest("operation sequence: next(), next(), next(), next()", () -> {
			var iterable = generator().create(samples());
			var actualIterator = iterable.iterator();
			var expectedElements = List.of(samples().e0(), samples().e1(), samples().e2());

			new IteratorOperationSequence(NEXT, NEXT, NEXT, NEXT).check(actualIterator, expectedElements);
		}), dynamicTest("operation sequence: next(), next(), hasNext(), next()", () -> {
			var iterable = generator().create(samples());
			var actualIterator = iterable.iterator();
			var expectedElements = List.of(samples().e0(), samples().e1(), samples().e2());

			new IteratorOperationSequence(NEXT, NEXT, HAS_NEXT, NEXT).check(actualIterator, expectedElements);
		}), dynamicTest("operation sequence: next(), next(), next(), hasNext()", () -> {
			var iterable = generator().create(samples());
			var actualIterator = iterable.iterator();
			var expectedElements = List.of(samples().e0(), samples().e1(), samples().e2());

			new IteratorOperationSequence(NEXT, NEXT, NEXT, HAS_NEXT).check(actualIterator, expectedElements);
		}));
	}

}
