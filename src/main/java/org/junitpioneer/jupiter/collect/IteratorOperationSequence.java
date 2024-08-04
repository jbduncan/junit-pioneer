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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final class IteratorOperationSequence {

	private final List<IteratorOperation> iteratorOperations;

	IteratorOperationSequence(IteratorOperation... iteratorOperations) {
		this.iteratorOperations = new ArrayList<>(List.of(iteratorOperations));
	}

	<E> void check(Iterator<E> actualIterator, List<E> remainingExpectedElements) {
		remainingExpectedElements = toMutableList(remainingExpectedElements);
		for (var iteratorOperation : iteratorOperations) {
			switch (iteratorOperation) {
				case HAS_NEXT:
					doHasNextOpAndCheck(actualIterator, remainingExpectedElements);
					break;
				case NEXT:
					doNextOpAndCheck(actualIterator, remainingExpectedElements);
					break;
				case REMOVE:
					throw new UnsupportedOperationException("TODO"); // TODO
			}
		}
	}

	private static <E> List<E> toMutableList(List<E> remainingExpectedElements) {
		return new ArrayList<>(remainingExpectedElements);
	}

	private <E> void doHasNextOpAndCheck(Iterator<E> actualIterator, List<E> remainingExpectedElements) {
		if (!remainingExpectedElements.isEmpty()) {
			// TODO: add a message parameter
			assertTrue(actualIterator.hasNext());
			return;
		}

		// TODO: add a message parameter
		assertFalse(actualIterator.hasNext());
	}

	private <E> void doNextOpAndCheck(Iterator<E> actualIterator, List<E> remainingExpectedElements) {
		if (remainingExpectedElements.isEmpty()) {
			// TODO: add a message parameter
			assertThrows(NoSuchElementException.class, actualIterator::next);
			return;
		}

		E nextValue = actualIterator.next();

		// TODO: add a message parameter
		assertEquals(remainingExpectedElements.get(0), nextValue);
		remainingExpectedElements.remove(0);

		// TODO: deal with "unknown order" iterators, where the ordering isn't defined or consistent, like with set iterators
	}

}
