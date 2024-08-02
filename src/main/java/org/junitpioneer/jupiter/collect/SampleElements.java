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

public final class SampleElements<E> {

	private final E e0;
	private final E e1;
	private final E e2;

	public static SampleElements<String> strings() {
		return new SampleElements<>("a", "b", "c");
	}

	private SampleElements(E e0, E e1, E e2) {
		this.e0 = e0;
		this.e1 = e1;
		this.e2 = e2;
	}

	public E e0() {
		return e0;
	}

	public E e1() {
		return e1;
	}

	public E e2() {
		return e2;
	}

}
