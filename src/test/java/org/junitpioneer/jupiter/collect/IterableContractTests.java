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

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junitpioneer.testkit.PioneerTestKit;

class IterableContractTests {

	static final class ExampleIterableContractTestCase implements StringIterableContract {

		@Override
		public TestIterableGenerator<String> generator() {
			return List::of;
		}

	}

	@Test
	void exampleIterableContract() {
		PioneerTestKit
				.executeTestClass(ExampleIterableContractTestCase.class)
				.allEvents()
				.assertStatistics(stats -> stats.dynamicallyRegistered(1));
	}

}
