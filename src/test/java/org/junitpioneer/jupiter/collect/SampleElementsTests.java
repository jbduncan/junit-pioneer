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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class SampleElementsTests {

	@Test
	void strings() {
		var strings = SampleElements.strings();
		assertAll(() -> assertThat(strings.e0()).isEqualTo("a"), () -> assertThat(strings.e1()).isEqualTo("b"),
			() -> assertThat(strings.e2()).isEqualTo("c"));
	}

}
