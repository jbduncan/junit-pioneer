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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class IteratorOperationSequence {

	private final List<IteratorOperation> iteratorOperations;

	IteratorOperationSequence(IteratorOperation... iteratorOperations) {
		this.iteratorOperations = new ArrayList<>(List.of(iteratorOperations));
	}

	<E> void check(Iterator<E> actualIterator, List<E> remainingExpectedElements) {
		for (var iteratorOperation : iteratorOperations) {
			switch (iteratorOperation) {
				case HAS_NEXT:
					doHasNextOpAndCheck(actualIterator, remainingExpectedElements);
					break;
				case VALUE:
					throw new UnsupportedOperationException("TODO"); // TODO
				case REMOVE:
					throw new UnsupportedOperationException("TODO"); // TODO
			}
		}
	}

	private <E> void doHasNextOpAndCheck(Iterator<E> actualIterator, List<E> remainingExpectedElements) {
		if (actualIterator.hasNext()) {
			// TODO: assert that remainingExpectedElements is empty
		}

		// TODO: assert that remainingExpectedElements is not empty
	}

}
