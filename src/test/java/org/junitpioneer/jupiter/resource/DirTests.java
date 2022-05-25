/*
 * Copyright 2016-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junitpioneer.jupiter.resource.Assertions.assertCanAddAndReadTextFile;
import static org.junitpioneer.jupiter.resource.Assertions.assertEmptyReadableWriteableTemporaryDirectory;
import static org.junitpioneer.testkit.assertion.PioneerAssert.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junitpioneer.testkit.ExecutionResults;
import org.junitpioneer.testkit.PioneerTestKit;

class DirTests {

	@DisplayName("when a test class has a test method with a @Dir-annotated parameter")
	@Nested
	class WhenTestClassHasTestMethodWithDirParameterTests {

		@DisplayName("then the parameter is populated with a new readable and writeable temporary directory "
				+ "that lasts as long as the test")
		@Test
		void thenParameterIsPopulatedWithNewReadableAndWriteableTempDirThatLastsAsLongAsTheTest() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(SingleTestMethodWithDirParameterTestCases.class);
			assertThat(executionResults).hasSingleSucceededTest();
			assertThat(SingleTestMethodWithDirParameterTestCases.recordedPath).doesNotExist();
		}

	}

	static class SingleTestMethodWithDirParameterTestCases {

		static Path recordedPath;

		@Test
		void theTest(@Dir Path tempDir) {
			assertEmptyReadableWriteableTemporaryDirectory(tempDir);
			assertCanAddAndReadTextFile(tempDir);

			recordedPath = tempDir;
		}

	}

}