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

enum IteratorOperation {

	HAS_NEXT {

		@Override
		public String toString() {
			return "hasNext()";
		}

	},
	NEXT {

		@Override
		public String toString() {
			return "next()";
		}

	},
	REMOVE {

		@Override
		public String toString() {
			return "remove()";
		}

	}

}
