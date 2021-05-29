Extension `ResourceManager` looks for fields and
parameters annotated with `@New` `@Share` and `@Shared`.

`@New` is a field/parameter-level annotation that accepts
a Class that implements `ResourceSupplier<T>`. For example,
`@New(TemporaryDirectory.class)`

`@Share` is a class-level annotation that accepts a Class
that implements `ResourceSupplier<T>`, as well as a String
name. For example,
`@Share(name = "aClassWideTempDirectory", value = TemporaryDirectory.class)`

`@Shared` is a field/paramter-level annotation that accepts
a String value. For example,
`@Shared("aClassWideTempDirectory")`

When a parameter in a test method is annotated with `@New`,
it checks if the given class implements `ResourceSupplier`
and has a no-args constructor. If so, it creates a new
instance of the `ResourceSupplier` implementation and asks
it for a `T` with `ResourceSupplier::get`. If `@New` is
used on a test method parameter, the associated
`ResourceSupplier` (and thus the contained `T`) only lasts
as long as the test does and is "torn down" straight
afterwards. If `@New` is used on a test class field, the
associated `ResourceSupplier` is instantiated anew before
and "torn down" immediately after each and every test
method in the test class.

When a test class is annotated with `@Share`, it checks if
the given class implements `ResourceSupplier` and has a
no-args constructor. If so, the `ResourceSupplier` instance
will be instantiated and saved until all the test methods
in that class have been run, at which point it will be
"torn down". The `ResourceSupplier` (and thus the contained
`T`) is saved with a key, where this key comes from
`@Share`'s String name field. This allows the `T` to be
reused across tests in the test class.

When a field or test method parameter is annotated with
`@Shared`, it finds a `@Share` annotation on the same test
class whose String name matches the `@Shared`'s String
value. If such a `@Share` can be found, then the field or 
parameter annotated with `@Shared` is populated with the 
`T` from the `ResourceSupplier` from the `@Share`.

When a `@New` or `@Share` is "torn down",
it calls the `ResourceSupplier` implementation's `close`
method on the field/parameter's instance of `T`.

For example:

```java
@ExtendWith(ResourceManager.class)
class FooTests {

  // Before each test method, this annotation creates a new
  // instance of `Path` that points to a new subdirectory of
  // the machine's temporary directory.
  //
  // After each test method, it is torn down, ready to be recreated
  // for the next test method.
  @New(TemporaryDirectory.class)
  Path firstDirectory;
  
  @Test
  void testFoo1(
      // Creates a `Path` pointing to another
      // temporary directory.
      //
      // It is created before this test method starts,
      // and is closed as soon as this method is finished.
      @New(TemporaryDirectory.class) Path secondDirectory,

      // @Dir is a shortcut for @New(TempDirectory.class).
      @Dir Path thirdDirectory,

      // Thus fourthDirectory is different to thirdDirectory.
      @Dir Path fourthDirectory, 
      
      // Creates a new resource for the duration of this
      // test method, which is provided by a new, user-defined
      // `InMemoryDirectory` resource supplier.
      // (See InMemoryDirectory class below.)
      @New(InMemoryDirectory.class) Path inMemoryDirectory) {
    // ...
  }
}

@ExtendWith(ResourceManager.class)
@Share(name = "aClassWideTempDirectory", value = TemporaryDirectory.class)
class BarTests {
  @Test
  void testBar(
      // The key "aClassWideTempDirectory" below references
      // the @Share annotation above.
      // Thus the temporary directory here...
      @Shared("aClassWideTempDirectory")
      Path aClassWideTempDirectory) {
    Files.writeString(aClassWideTempDirectory.resolve("bar.txt"), "bar1");
  }
  
  @Test
  void testBar2(
      // ...is the same temporary directory as here!
      @Shared("aClassWideTempDirectory")
      Path aClassWideTempDirectory) {
    Files.writeString(aClassWideTempDirectory.resolve("bar.txt"), "bar2");
  }
}

// At the end, "aClassWideTempDirectory" will have a
// "bar.txt" file with lines "bar1" and "bar2".
```

```java
// This ResourceSupplier comes out of the box with this extension.
public final class TemporaryDirectory implements ResourceSupplier<Path> {
  
  private final Path path;
  
  public TemporaryDirectory() {
    // creates a new subdirectory on the machine-wide
    // temporary directory
    this.path = ...
  }
  
  @Override
  public Path get() {
    // returns the subdirectory above
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
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

// An example of a user-defined resource supplier that uses Google's
// jimfs in-memory filesystem. Used in FooTests above.
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

// We can even create a resource supplier that holds an OkHttp mock web server!
public final class WebServer implements ResourceSupplier<Path> {
    
  // Another example of a resource that eventually needs to be closed.
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

// Example test
@ExtendWith(ResourceManager.class)
class BazTests {
  @Test
  void testBaz(
      @New(WebServer.class) MockWebServer mockWebServer) {
    // given
    mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            return new MockResponse().setResponseCode(404);
          }
        });
    
    // when
    var client = new MyOwnHttpClient(mockWebServer.url("/"));
    MyOwnHttpClientResponse response = client.get();
    
    // then
    assertTrue(response.is404());
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