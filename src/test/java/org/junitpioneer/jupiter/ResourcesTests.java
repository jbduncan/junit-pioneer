/*
 * Copyright 2016-2021 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer.jupiter;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.testkit.engine.EventConditions.finished;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.cause;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.instanceOf;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.throwable;
import static org.junitpioneer.testkit.assertion.PioneerAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.UnknownHostException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junitpioneer.internal.TestExtensionContext;
import org.junitpioneer.testkit.ExecutionResults;
import org.junitpioneer.testkit.PioneerTestKit;

@DisplayName("Resources extension")
class ResourcesTests {

	@DisplayName("when a test class has a test method with a @New(TemporaryDirectory.class)-annotated parameter")
	@Nested
	class WhenTestClassHasTestMethodWithNewTempDirParameterTests {

		@DisplayName("then the parameter is populated with a new readable and writeable temporary directory "
				+ "that lasts as long as the test")
		@Test
		void thenParameterIsPopulatedWithNewReadableAndWriteableTempDirThatLastsAsLongAsTheTest() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(SingleTestMethodWithNewTempDirParameterTestCase.class);
			assertThat(executionResults).hasSingleSucceededTest();
			assertThat(SingleTestMethodWithNewTempDirParameterTestCase.recordedPath).doesNotExist();
		}

	}

	@Resources
	static class SingleTestMethodWithNewTempDirParameterTestCase {

		static Path recordedPath;

		@Test
		void theTest(@New(TemporaryDirectory.class) Path tempDir) {
			assertEmptyReadableWriteableTemporaryDirectory(tempDir);
			assertCanAddAndReadTextFile(tempDir);

			recordedPath = tempDir;
		}

	}

	// ---

	@DisplayName("when a test class has a test method with a parameter annotated with "
			+ "@New(value = TemporaryDirectory.class, arguments = {\"tempDirPrefix\"}")
	@Nested
	class WhenTestClassHasTestMethodWithParameterAnnotatedWithNewTempDirWithArg {

		@DisplayName("then the parameter is populated with a new temporary directory "
				+ "that has the prefix \"tempDirPrefix\"")
		@Test
		void thenParameterIsPopulatedWithNewTempDirWithSuffixEquallingArg() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(SingleTestMethodWithParameterWithNewTempDirAndArgTestCase.class);
			assertThat(executionResults).hasSingleSucceededTest();
		}

	}

	@Resources
	static class SingleTestMethodWithParameterWithNewTempDirAndArgTestCase {

		@Test
		void theTest(@New(value = TemporaryDirectory.class, arguments = { "tempDirPrefix" }) Path tempDir) {
			assertThat(ROOT_TMP_DIR.relativize(tempDir)).asString().startsWith("tempDirPrefix");
		}

	}

	// ---

	@DisplayName("when a test class has multiple test methods with a @New(TemporaryDirectory.class)-annotated parameter")
	@Nested
	class WhenTestClassHasMultipleTestMethodsWithNewTempDirAnnotatedParameterTests {

		@DisplayName("then the parameters on both test methods are populated with new readable and writeable "
				+ "temporary directories that are torn down afterwards")
		@Test
		void thenParametersOnBothTestMethodsArePopulatedWithNewReadableAndWriteableTempDirsThatAreTornDownAfterwards() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(TwoTestMethodsWithNewTempDirParameterTestCase.class);
			assertThat(executionResults).hasNumberOfSucceededTests(2);
			assertThat(TwoTestMethodsWithNewTempDirParameterTestCase.recordedPaths)
					.hasSize(2)
					.doesNotHaveDuplicates()
					.allSatisfy(path -> assertThat(path).doesNotExist());
		}

	}

	@Resources
	static class TwoTestMethodsWithNewTempDirParameterTestCase {

		static List<Path> recordedPaths = new CopyOnWriteArrayList<>();

		@Test
		void firstTest(@New(TemporaryDirectory.class) Path tempDir) {
			assertEmptyReadableWriteableTemporaryDirectory(tempDir);

			recordedPaths.add(tempDir);
		}

		@Test
		void secondTest(@New(TemporaryDirectory.class) Path tempDir) {
			assertEmptyReadableWriteableTemporaryDirectory(tempDir);

			recordedPaths.add(tempDir);
		}

	}

	// ---

	@DisplayName("when a test class has a test method with multiple @New(TemporaryDirectory.class)-annotated parameters")
	@Nested
	class WhenTestClassHasTestMethodWithMultipleNewTempDirAnnotatedParameterTests {

		@DisplayName("then the parameters on the test method are populated with new readable and writeable "
				+ "temporary directories that are torn down afterwards")
		@Test
		void thenParametersOnTheTestMethodArePopulatedWithNewReadableAndWriteableTempDirsThatAreTornDownAfterwards() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(SingleTestMethodWithTwoNewTempDirParametersTestCase.class);
			assertThat(executionResults).hasSingleSucceededTest();
			assertThat(SingleTestMethodWithTwoNewTempDirParametersTestCase.recordedPaths)
					.hasSize(2)
					.doesNotHaveDuplicates()
					.allSatisfy(path -> assertThat(path).doesNotExist());
		}

	}

	@Resources
	static class SingleTestMethodWithTwoNewTempDirParametersTestCase {

		static List<Path> recordedPaths = new CopyOnWriteArrayList<>();

		@Test
		void firstTest(@New(TemporaryDirectory.class) Path firstTempDir,
				@New(TemporaryDirectory.class) Path secondTempDir) {
			assertEmptyReadableWriteableTemporaryDirectory(firstTempDir);
			assertEmptyReadableWriteableTemporaryDirectory(secondTempDir);

			recordedPaths.addAll(asList(firstTempDir, secondTempDir));
		}

	}

	// ---

	@DisplayName("when a test class has a test method with a @New(InMemoryDirectory.class)-annotated parameter")
	@Nested
	class WhenTestClassHasTestMethodWithNewInMemoryDirParameterTests {

		@DisplayName("then the parameter is populated with a new readable and writeable temporary directory "
				+ "that lasts as long as the test")
		@Test
		void thenParameterIsPopulatedWithNewReadableAndWriteableTempDirThatLastsAsLongAsTheTest() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(SingleTestMethodWithNewInMemoryDirParameterTestCase.class);
			assertThat(executionResults).hasSingleSucceededTest();
			assertThatThrownBy(
				() -> Files.readAllLines(SingleTestMethodWithNewInMemoryDirParameterTestCase.recordedPath))
						.isInstanceOf(ClosedFileSystemException.class);
		}

	}

	@Resources
	static class SingleTestMethodWithNewInMemoryDirParameterTestCase {

		static Path recordedPath;

		@Test
		void theTest(@New(InMemoryDirectory.class) Path inMemoryDir) {
			assertEmptyReadableWriteableInMemoryDirectory(inMemoryDir);
			assertCanAddAndReadTextFile(inMemoryDir);

			recordedPath = inMemoryDir;
		}

	}

	static class InMemoryDirectory implements ResourceFactory<Path> {

		private final FileSystem inMemoryFileSystem = Jimfs.newFileSystem(Configuration.unix());

		@Override
		public Resource<Path> create() {
			return () -> {
				Path result = inMemoryFileSystem.getPath("test");
				Files.createDirectory(result);
				return result;
			};
		}

		@Override
		public void close() throws Exception {
			inMemoryFileSystem.close();
		}

	}

	// ---

	@DisplayName("when a test class has a constructor with a @New(TemporaryDirectory.class)-annotated parameter")
	@Nested
	class WhenTestClassHasConstructorWithNewTemporaryDirectoryAnnotatedParameterTests {

		@DisplayName("then each test method has access to a new readable and writeable temporary directory "
				+ "that lasts as long as the test instance")
		@Test
		void thenEachTestMethodHasAccessToNewReadableAndWriteableTempDirThatLastsAsLongAsTestInstance() {
			ExecutionResults executionResults = PioneerTestKit
					.executeTestClass(TestConstructorWithNewTempDirParameterTestCase.class);
			assertThat(executionResults).hasNumberOfSucceededTests(2);
			assertThat(TestConstructorWithNewTempDirParameterTestCase.recordedPathsFromConstructor)
					.hasSize(2)
					.doesNotHaveDuplicates()
					.allSatisfy(path -> assertThat(path).doesNotExist());
		}

	}

	@Resources
	static class TestConstructorWithNewTempDirParameterTestCase {

		static List<Path> recordedPathsFromConstructor = new CopyOnWriteArrayList<>();
		Path recordedPath;

		TestConstructorWithNewTempDirParameterTestCase(@New(TemporaryDirectory.class) Path tempDir) {
			recordedPathsFromConstructor.add(tempDir);
			recordedPath = tempDir;
		}

		@Test
		void firstTest() {
			assertEmptyReadableWriteableTemporaryDirectory(recordedPath);
		}

		@Test
		void secondTest() {
			assertEmptyReadableWriteableTemporaryDirectory(recordedPath);
		}

	}

	// ---

	// TODO: Uncomment this test

	//	@DisplayName("when a test class has a test method with a @Shared(TemporaryDirectory.class)-annotated parameter")
	//	@Nested
	//	class WhenTestClassHasTestMethodWithSharedTempDirParameterTests {
	//		@DisplayName("then the parameter is populated with a new readable and writeable temporary directory "
	//				+ "that lasts the entire test run")
	//		@Test
	//		void thenParameterIsPopulatedWithNewReadableAndWriteableTempDirThatLastsTheEntireTestRun() {
	//			ExecutionResults executionResults = PioneerTestKit.executeTestClass(SingleTestMethodWithSharedTempDirParameterTestCase.class);
	//			assertThat(executionResults).hasSingleSucceededTest();
	//			assertThat(SingleTestMethodWithSharedTempDirParameterTestCase.recordedPath).doesNotExist();
	//		}
	//	}
	//
	//	@Resources
	//	static class SingleTestMethodWithSharedTempDirParameterTestCase {
	//
	//		static Path recordedPath;
	//
	//		@Test
	//		void theTest(@Shared(value = TemporaryDirectory.class, name = "uniqueTempDirName") Path tempDir) {
	//			assertEmptyReadableWriteableTemporaryDirectory(tempDir);
	//			assertCanAddAndReadTextFile(tempDir);
	//
	//			recordedPath = tempDir;
	//		}
	//
	//	}

	// ---

	// TODO: Write a test that checks when two or more test classes are run, then a @Shared resource is used
	//       across both of them.
	//       Use JUnit 5's own EngineTestKit for this, or adapt PioneerTestKit to accept many classes.

	@DisplayName("when Resources is applied to a test method with an unannotated parameter")
	@Nested
	class WhenResourcesIsAppliedToTestMethodWithUnannotatedParameterTests {

		@DisplayName("then Resources does not populate the parameter")
		@Test
		void thenSupportsParameterReturnsTrue() {
			ExecutionResults executionResults = PioneerTestKit.executeTestClass(UnannotatedParameterTestCase.class);
			executionResults
					.testEvents()
					.debug()
					.assertThatEvents()
					.haveExactly(1,
						finished(throwable(message(
							m -> m.startsWith("No ParameterResolver registered for parameter [java.lang.Object") && m
									.endsWith("in method [void org.junitpioneer.jupiter."
											+ "ResourcesTests$UnannotatedParameterTestCase."
											+ "theTest(java.lang.Object)].")))));
		}

	}

	@Resources
	@SuppressWarnings("unused")
	static class UnannotatedParameterTestCase {

		@Test
		void theTest(Object randomParameter) {

		}

	}

	// ---

	@DisplayName("when new resource factory is applied to a parameter")
	@Nested
	class WhenNewResourceFactoryAppliedToParameterTests {

		@DisplayName("and the factory throws on ::create")
		@Nested
		class AndFactoryThrowsOnCreateTests {

			@DisplayName("then the thrown exception is wrapped and propagated")
			@Test
			void thenThrownExceptionIsWrappedAndPropagated() {
				ExecutionResults executionResults = PioneerTestKit
						.executeTestClass(ThrowOnCreateResourceFactoryTestCase.class);
				executionResults
						.testEvents()
						.debug()
						.assertThatEvents()
						.haveExactly(//
							1, //
							finished(//
								throwable(//
									instanceOf(ParameterResolutionException.class), //
									message(
										"Unable to create an instance of `" + ThrowOnCreateResourceFactory.class + "`"), //
									cause(//
										instanceOf(EXPECTED_THROW_ON_CREATE_RESOURCE_FACTORY_EXCEPTION.getClass()), //
										message(EXPECTED_THROW_ON_CREATE_RESOURCE_FACTORY_EXCEPTION.getMessage())))));
			}

		}

		@DisplayName("and the factory throws on ::close")
		@Nested
		class AndFactoryThrowsOnCloseTests {

			@DisplayName("then the thrown exception is propagated")
			@Test
			void thenThrownExceptionIsPropagated() {
				ExecutionResults executionResults = PioneerTestKit
						.executeTestClass(ThrowOnCloseResourceFactoryTestCase.class);
				executionResults
						.allEvents()
						.debug()
						.assertThatEvents()
						.haveExactly(//
							1, //
							finished(//
								throwable(//
									instanceOf(EXPECTED_THROW_ON_CLOSE_RESOURCE_FACTORY_EXCEPTION.getClass()), //
									message(EXPECTED_THROW_ON_CLOSE_RESOURCE_FACTORY_EXCEPTION.getMessage()))));
			}

		}

		@DisplayName("and a new resource is created")
		@Nested
		class AndNewResourceIsCreatedTests {

			@DisplayName("and the resource throws on ::get")
			@Nested
			class AndResourceThrowsOnGetTests {

				@DisplayName("then the thrown exception is wrapped and propagated")
				@Test
				void thenThrownExceptionIsWrappedAndPropagated() {
					ExecutionResults executionResults = PioneerTestKit
							.executeTestClass(ThrowOnGetResourceTestCase.class);
					executionResults
							.testEvents()
							.debug()
							.assertThatEvents()
							.haveExactly(//
								1, //
								finished(//
									throwable(//
										instanceOf(ParameterResolutionException.class), //
										message("Unable to create an instance of `" + ThrowOnGetResource.class + "`"), //
										cause(//
											instanceOf(EXPECTED_THROW_ON_GET_RESOURCE_EXCEPTION.getClass()), //
											message(EXPECTED_THROW_ON_GET_RESOURCE_EXCEPTION.getMessage())))));
				}

			}

			@DisplayName("and the resource throws on ::close")
			@Nested
			class AndResourceThrowsOnCloseTests {

				@DisplayName("then the thrown exception is propagated")
				@Test
				void thenThrownExceptionIsWrappedAndPropagated() {
					ExecutionResults executionResults = PioneerTestKit
							.executeTestClass(ThrowOnCloseResourceResourceFactoryTestCase.class);
					executionResults
							.testEvents()
							.debug()
							.assertThatEvents()
							.haveExactly(//
								1, //
								finished(//
									throwable(//
										instanceOf(EXPECTED_THROW_ON_CLOSE_RESOURCE_EXCEPTION.getClass()), //
										message(EXPECTED_THROW_ON_CLOSE_RESOURCE_EXCEPTION.getMessage()))));
				}

			}

		}

	}

	@Resources
	static class ThrowOnCreateResourceFactoryTestCase {

		@Test
		@SuppressWarnings("unused")
		void foo(@New(ThrowOnCreateResourceFactory.class) Object object) {

		}

	}

	static final class ThrowOnCreateResourceFactory implements ResourceFactory<Object> {

		@Override
		public Resource<Object> create() throws Exception {
			throw EXPECTED_THROW_ON_CREATE_RESOURCE_FACTORY_EXCEPTION;
		}

	}

	private static final Exception EXPECTED_THROW_ON_CREATE_RESOURCE_FACTORY_EXCEPTION = new IOException(
		"failed to connect to the Matrix");

	@Resources
	static class ThrowOnCloseResourceFactoryTestCase {

		@Test
		@SuppressWarnings("unused")
		void foo(@New(ThrowOnCloseResourceFactory.class) Object object) {

		}

	}

	static final class ThrowOnCloseResourceFactory implements ResourceFactory<Object> {

		@Override
		public Resource<Object> create() {
			return () -> "foo";
		}

		@Override
		public void close() throws Exception {
			throw EXPECTED_THROW_ON_CLOSE_RESOURCE_FACTORY_EXCEPTION;
		}

	}

	private static final Exception EXPECTED_THROW_ON_CLOSE_RESOURCE_FACTORY_EXCEPTION = new CloneNotSupportedException(
		"failed to clone a homunculus");

	@Resources
	static class ThrowOnGetResourceTestCase {

		@Test
		@SuppressWarnings("unused")
		void foo(@New(ThrowOnGetResourceResourceFactory.class) Object object) {

		}

	}

	static final class ThrowOnGetResourceResourceFactory implements ResourceFactory<Object> {

		@Override
		public Resource<Object> create() {
			return new ThrowOnGetResource();
		}

	}

	static final class ThrowOnGetResource implements Resource<Object> {

		@Override
		public Object get() throws Exception {
			throw EXPECTED_THROW_ON_GET_RESOURCE_EXCEPTION;
		}

	}

	private static final Exception EXPECTED_THROW_ON_GET_RESOURCE_EXCEPTION = new FileAlreadyExistsException(
		"wait, what's that file doing there?");

	@Resources
	static class ThrowOnCloseResourceResourceFactoryTestCase {

		@Test
		@SuppressWarnings("unused")
		void foo(@New(ThrowOnCloseResourceResourceFactory.class) Object object) {

		}

	}

	static final class ThrowOnCloseResourceResourceFactory implements ResourceFactory<Object> {

		@Override
		public Resource<Object> create() {
			return new Resource<Object>() {

				@Override
				public Object get() {
					return "foo";
				}

				@Override
				public void close() throws Exception {
					throw EXPECTED_THROW_ON_CLOSE_RESOURCE_EXCEPTION;
				}

			};
		}

	}

	private static final Exception EXPECTED_THROW_ON_CLOSE_RESOURCE_EXCEPTION = new UnknownHostException(
		"wait, where's the Internet gone?!");

	// ---

	// TODO: It's generally good advice to avoid mocking things that we, JUnit Pioneer, don't own, because if we mock
	//       third-party things, then their real behaviour is likely to be updated over time and get out-of-sync with
	//       our mocks.
	//       Do we care about this, given that JUnit 5 avoids backwards-incompatible changes and that we're so closely
	//       tied to JUnit 5 itself?

	@DisplayName("when ResourceManagerExtension is unable to find @New on a parameter")
	@Nested
	class WhenResourceManagerExtensionUnableToFindNewOnParameterTests {

		@DisplayName("then an exception mentioning the parameter and the test method it's on is thrown")
		@Test
		void thenExceptionMentioningParameterAndTestMethodItsOnIsThrown() {
			// TODO: Consider introducing a TestParameterContext, similar to TestExtensionContext
			ParameterContext mockParameterContext = mock(ParameterContext.class);
			when(mockParameterContext.findAnnotation(New.class)).thenReturn(Optional.empty());
			Class<?> exampleClass = String.class;
			Method exampleMethod = ReflectionSupport.findMethod(exampleClass, "valueOf", Object.class).get();
			Parameter exampleParameter = exampleMethod.getParameters()[0];
			when(mockParameterContext.getParameter()).thenReturn(exampleParameter);

			assertThatThrownBy(() -> new ResourceManagerExtension()
					.resolveParameter(mockParameterContext, new TestExtensionContext(exampleClass, exampleMethod)))
							.isInstanceOf(ParameterResolutionException.class)
							.hasMessage("Parameter `" + exampleParameter + "` on method `" + exampleMethod
									+ "` is not annotated with @New");
		}

		@DisplayName("and the test method does not exist")
		@Nested
		class AndTestMethodDoesNotExistTests {

			@DisplayName("then an exception mentioning just the parameter is thrown")
			@Test
			void thenExceptionMentioningJustParameterIsThrown() {
				ParameterContext mockParameterContext = mock(ParameterContext.class);
				when(mockParameterContext.findAnnotation(New.class)).thenReturn(Optional.empty());
				Class<?> exampleClass = String.class;
				Method exampleMethod = ReflectionSupport.findMethod(exampleClass, "valueOf", Object.class).get();
				Parameter exampleParameter = exampleMethod.getParameters()[0];
				when(mockParameterContext.getParameter()).thenReturn(exampleParameter);

				assertThatThrownBy(() -> new ResourceManagerExtension()
						.resolveParameter(mockParameterContext, new TestExtensionContext(null, null)))
								.isInstanceOf(ParameterResolutionException.class)
								.hasMessage("Parameter `" + exampleParameter
										+ "` on unknown method is not annotated with @New");
			}

		}

	}

	// ---

	// TODO: Write and test with two custom ResourceFactory implementations: jimfs and OkHttp's MockWebServer

	// ---

	@DisplayName("check that all Resources-related classes are final")
	@Test
	void checkThatAllResourcesRelatedClassesAreFinal() {
		assertThat(TemporaryDirectory.class).isFinal();
		assertThat(ResourceManagerExtension.class).isFinal();
		// TODO: Add the jimfs and OkHttp MockServer-based resource factories here
	}

	// ---

	private static void assertEmptyReadableWriteableTemporaryDirectory(Path tempDir) {
		assertThat(tempDir).isEmptyDirectory().startsWith(ROOT_TMP_DIR).isReadable().isWritable();
	}

	private static void assertEmptyReadableWriteableInMemoryDirectory(Path tempDir) {
		assertThat(tempDir).isEmptyDirectory().isReadable().isWritable();
		try (FileSystem fileSystem = Jimfs.newFileSystem()) {
			assertThat(tempDir.getFileSystem()).isInstanceOf(fileSystem.getClass());
		}
		catch (IOException e) {
			fail(e);
		}
	}

	private static void assertCanAddAndReadTextFile(Path tempDir) {
		assertDoesNotThrow(() -> Files.write(tempDir.resolve("some-file.txt"), singletonList("some-text")));
		List<String> lines = assertDoesNotThrow(() -> Files.readAllLines(tempDir.resolve("some-file.txt")));
		assertThat(lines).containsExactly("some-text");
	}

	private static final Path ROOT_TMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"));

}
