/*
 * Copyright 2016-2022 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.testkit.assertion;

import java.nio.file.Path;

import org.junitpioneer.testkit.ExecutionResults;

/**
 * Entry point to all JUnit Pioneer assertions.
 */
public class PioneerAssert {

	private PioneerAssert() {
		// private constructor to prevent instantiation
	}

	public static ExecutionResultAssert assertThat(ExecutionResults actual) {
		return new PioneerExecutionResultAssert(actual);
	}

	// while AssertJ assertion entry points are usually called `assertThat`,
	// this method needs a different name to prevent conflicts with the
	// official `Assertions.assertThat(Path)` when both are statically imported
	public static PioneerPathAssert assertThatPath(Path actual) {
		return new PioneerPathAssert(actual);
	}

}
