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

import static org.junit.platform.testkit.engine.EventConditions.dynamicTestRegistered;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedSuccessfully;
import static org.junit.platform.testkit.engine.EventConditions.started;
import static org.junit.platform.testkit.engine.EventConditions.test;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.testkit.PioneerTestKit;

class IterableContractTests {

	static final class IterableContractForListOfTestCase implements StringIterableContract {

		@Override
		public TestIterableGenerator<String> generator() {
			return List::of;
		}

	}

	@Test
	@DisplayName("IterableContract for List::of")
	void iterableContractForListOf() {
		PioneerTestKit
				.executeTestClass(IterableContractForListOfTestCase.class)
				.testEvents()
				.assertStatistics(stats -> stats.dynamicallyRegistered(1))
				.assertEventsMatchExactly( //
					event(dynamicTestRegistered("dynamic-test:#1")), //
					event(test("dynamic-test:#1", "operation sequence: next(), next(), next(), next(), next(), next()"),
						started()), //
					event(test("dynamic-test:#1", "operation sequence: next(), next(), next(), next(), next(), next()"),
						finishedSuccessfully()));
	}

}
