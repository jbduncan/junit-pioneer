```java
// Extension `ResourceManager` looks for fields and
// parameters annotated with `@New`, `@Shared` or
// `@Singleton`.
//
// `@New` accepts a Class that implements `ResourceSupplier<T>`.
// For example, `@New(TemporaryDirectory.class)`
//
// `@Shared` accepts a Class that implements
// `ResourceSupplier<T>`, as well as a String name.
// For example, `@Shared(name = "firstGlobalTempDir", TemporaryDirectory.class)`
//
// When a field in a test class or a parameter in a test method
// method is annotated with `@New`, it checks if the given class
// implements `ResourceSupplier` and has a no-args constructor.
// If so, it creates a new instance of the `ResourceSupplier`
// implementation and asks it for a `T` with
// `ResourceSupplier::get`.
//
// When a field/parameter is annotated with `@Shared`, the
// `ResourceSupplier` instance will be instantiated and saved
// until test plan run finishes. This allows `T`s to be reused
// across tests.
//
// When a field or parameter goes "out of scope", it calls the
// `ResourceSupplier` implementation's `close` method on the
// field/parameter's instance of `T`.
//
// A field is out of scope if the associated test class has been
// torn down by JUnit 5.
// (By default, JUnit 5 creates a new instance of the test class
// for each test method in the class, but the same test class
// instance can be used for all test methods with
// [`@TestInstance(Lifecycle.PER_CLASS)`](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle).)
//
// A parameter is out of scope if its associated test method has
// finished.
@ExtendWith(ResourceManager.class)
class FooTests {
  
  final Path firstDirectory;

  // Creates an instance of `Path` that points to a new
  // subdirectory of the machine's temporary directory.
  //
  // It is closed when the `FooTests` instance is torn down
  // by JUnit 5.
  FooTests(@New(TemporaryDirectory.class) Path firstDirectory) {
    this.firstDirectory = firstDirectory;
  }
  
  // Creates an instance of `Path` that points to a
  // second temporary subdirectory.
  @New(TemporaryDirectory.class)
  final Path secondDirectory;
  
  @Test
  void testFoo(
      // Creates a `Path` pointing to a temporary directory,
      // which is closed when `testFoo` is finished.
      @New(TemporaryDirectory.class) Path thirdDirectory,

      // @Dir is a shortcut for @New(TempDirectory.class).
      @Dir Path fourthDirectory,

      // Thus fifthDirectory is different to fourthDirectory.
      @Dir Path fifthDirectory, 
      
      // Creates a new resource provided by a new instance of
      // a user-defined `InMemoryDirectory` resource supplier.
      // (See InMemoryDirectory class below.)
      @New(InMemoryDirectory.class) Path inMemoryDirectory) {
    // ...
  }
}
```

```java
// Comes out of the box.
public final class TemporaryDirectory implements ResourceSupplier<Path> {
  
  private final Path path;
  
  public TemporaryDirectory() {
    // creates a new subdirectory on the machine-wide
    // temporary directory
    this.path = ...
  }
  
  @Override
  public Path get() {
    // returns a new subdirectory on the machine-wide
    // temporary directory
    return path;
  }
  
  @Override
  public void close() {
    // deletes the subdirectory
    deleteRecursively(path);
  }
  
  private static void deleteRecursively(Path path) {
    // ...
  }
}
```

```java
// An example of a user-defined resource supplier.
public final class InMemoryDirectory implements ResourceSupplier<Path> {
  
  // An example of a "resource" that must eventually be closed
  // to prevent resource starvation.
  private final FileSystem jimFs;
  
  public InMemoryDirectory() {
    this.jimFs = JimFs.newFileSystem(Configuration.unix());
  }
  
  @Override
  public Path get() {
    return jim.getPath("/");
  }
  
  @Override
  public void close() {
    try {
      jim.close();
    } catch (Exception e) {
      throw new RuntimeException("Cannot close in-memory filesystem", e);
    }
  }
  
}
```

```java
import mockwebserver3.MockWebServer;

// We can even create a resource supplier that points to an okhttp mock web server!
public final class WebServer implements ResourceSupplier<Path> {
  
  private final MockWebServer mockWebServer;
  
  public WebServer() {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
  }
  
  @Override
  public MockWebServer get() {
    return mockWebServer;
  }
  
  @Override
  public void close() {
    mockWebServer.close();
  }
  
}
```

```java
// The basic building block of this extension.
public interface ResourceSupplier<T> {

  /**
   * Returns an instance of <code>T</code> pointing to a resource managed by
   * <code>this</code> instance of <code>ResourceSupplier</code>.
   * 
   * @return an instance of <code>T</code>.
   */
  T get();

  /**
   * Closes the underlying resource associated with the <code>T</code>
   * returned by {@link #get()}.
   */
  void close();
}
```