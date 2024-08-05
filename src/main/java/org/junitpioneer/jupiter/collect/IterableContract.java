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
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junitpioneer.internal.PioneerUtils.genericCartesianProduct;
import static org.junitpioneer.jupiter.collect.IteratorOperation.HAS_NEXT;
import static org.junitpioneer.jupiter.collect.IteratorOperation.NEXT;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public interface IterableContract<E> {

	TestIterableGenerator<E> generator();

	SampleElements<E> samples();

	default Set<Feature<Iterable<E>>> features() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@TestFactory
	default List<DynamicTest> iterableTests() {

		// TODO: Give each dynamic test a URI Test Source:
		//  https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests-uri-test-source

		int operationSequenceSize = 4;
		var operations = List.of(HAS_NEXT, NEXT);
		List<List<IteratorOperation>> allOperationSequences = //
			genericCartesianProduct(nCopies(operationSequenceSize, operations));

		return DynamicTest
				.stream( //
					allOperationSequences.stream(), //
					IterableContract::operationSequenceDisplayName, //
					this::testOperationSequence)
				.collect(toUnmodifiableList());
	}

	private static String operationSequenceDisplayName(List<IteratorOperation> operationSequence) {
		return "operation sequence: "
				+ operationSequence.stream().map(IteratorOperation::toString).collect(joining(", "));
	}

	private void testOperationSequence(List<IteratorOperation> operationSequence) {
		var iterable = generator().create(samples());
		var actualIterator = iterable.iterator();
		var expectedElements = samples();

		new IteratorOperationSequenceChecker<>(actualIterator, operationSequence, expectedElements).check();
	}

}
