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

final class IteratorOperationSequenceChecker<E> {

	private final Iterator<E> iterator;
	private final List<IteratorOperation> operationSequence;
	private final SampleElements<E> expectedElements;

	IteratorOperationSequenceChecker(Iterator<E> iterator, List<IteratorOperation> operationSequence,
			SampleElements<E> expectedElements) {
		this.iterator = iterator;
		this.operationSequence = new ArrayList<>(operationSequence);
		this.expectedElements = expectedElements;
	}

	void check() {
		var remainingExpectedElements = toMutableList(expectedElements);
		for (var operation : operationSequence) {
			switch (operation) {
				case HAS_NEXT:
					doHasNextOpAndCheck(remainingExpectedElements);
					break;
				case NEXT:
					doNextOpAndCheck(remainingExpectedElements);
					break;
				case REMOVE:
					throw new UnsupportedOperationException("TODO"); // TODO
			}
		}
	}

	private static <E> List<E> toMutableList(SampleElements<E> samples) {
		return new ArrayList<>(List.of(samples.e0(), samples.e1(), samples.e2()));
	}

	private void doHasNextOpAndCheck(List<E> remainingExpectedElements) {
		if (remainingExpectedElements.isEmpty()) {
			// TODO: add a message parameter
			assertFalse(iterator.hasNext());
			return;
		}

		// TODO: add a message parameter
		assertTrue(iterator.hasNext());
	}

	private void doNextOpAndCheck(List<E> remainingExpectedElements) {
		if (remainingExpectedElements.isEmpty()) {
			// TODO: add a message parameter
			assertThrows(NoSuchElementException.class, iterator::next);
			return;
		}

		E nextValue = iterator.next();

		// TODO: add a message parameter
		assertEquals(remainingExpectedElements.get(0), nextValue);
		remainingExpectedElements.remove(0);

		// TODO: deal with "unknown order" iterators, where the ordering isn't defined or consistent, like with set iterators
	}

}
