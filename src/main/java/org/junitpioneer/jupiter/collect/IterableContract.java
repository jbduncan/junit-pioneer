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
	default Stream<DynamicNode> iterable() {
		return Stream.of(dynamicTest("foo", () -> {
		}));
	}

}
