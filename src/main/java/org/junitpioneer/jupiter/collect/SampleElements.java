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

public class SampleElements<E> {

	public static SampleElements<String> strings() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	private static <E> SampleElements<E> of(E e0, E e1, E e2, E missing) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	private SampleElements(E e0, E e1, E e2, E missing) {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public E e0() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public E e1() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public E e2() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * This element is never put into a collection for testing. It is used in tests that check that a
	 * given collection <i>does not</i> contain a certain element.
	 */
	public E missing() {
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
