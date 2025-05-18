# SpringLite - Lightweight Dependency Injection Framework

SpringLite is a minimalist dependency injection framework inspired by Spring Framework. It provides a deep understanding of how dependency injection works under the hood while maintaining a clean and simple API.

## Overview

SpringLite implements core dependency injection features:

- **Bean Management**: Define, register, and retrieve beans
- **Multiple Configuration Options**: XML-based or annotation-based
- **Flexible Injection Methods**: Constructor, setter, and field injection
- **Bean Lifecycle Management**: Singleton and prototype scopes
- **Robust Error Handling**: Clear error messages for common issues

## Getting Started

### Prerequisites

- Java 21 or later
- Maven

### Building the Project

```bash
# Clone the repository
git clone https://github.com/yourusername/springlite.git

# Build with Maven
cd springlite
mvn clean install
```

## Using SpringLite

### 1. XML-Based Configuration

Create an XML configuration file (`beans.xml`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <!-- Simple bean definition -->
    <bean id="userService" class="com.example.UserService" />

    <!-- Bean with constructor injection -->
    <bean id="userController" class="com.example.UserController">
        <property name="userService" ref="userService" />
    </bean>

    <!-- Bean with property values -->
    <bean id="configService" class="com.example.ConfigService">
        <property name="serverUrl" value="https://api.example.com" />
        <property name="maxConnections" value="100" />
    </bean>

    <!-- Bean with prototype scope -->
    <bean id="requestHandler" class="com.example.RequestHandler" scope="prototype" />
</beans>
```

Load the beans in your application:

```java
import com.bellagnech.springlite.di.ApplicationContext;
import com.bellagnech.springlite.di.XmlApplicationContext;

public class Application {
    public static void main(String[] args) {
        try {
            // Initialize the context with XML configuration
            ApplicationContext context = new XmlApplicationContext("path/to/beans.xml");

            // Get a bean by ID
            UserService userService = (UserService) context.getBean("userService");

            // Get a bean with type safety
            UserController controller = context.getBean("userController", UserController.class);

            // Use the beans
            controller.createUser("john.doe", "John Doe");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 2. Annotation-Based Configuration

Define your beans with annotations:

```java
import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Qualifier;
import com.bellagnech.springlite.di.annotations.Scope;

// Simple component
@Component
public class UserService {
    public User findUser(String username) {
        // Implementation
    }
}

// Component with constructor injection
@Component
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Business methods
}

// Component with setter injection
@Component
public class ProductService {
    private CategoryService categoryService;

    @Autowired
    public void setCategoryService(CategoryService service) {
        this.categoryService = service;
    }
}

// Component with field injection
@Component
public class OrderService {
    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("jpaRepository")
    private OrderRepository orderRepository;
}

// Prototype-scoped component
@Component
@Scope("prototype")
public class RequestHandler {
    // A new instance will be created each time it's requested
}
```

Load the beans in your application:

```java
import com.bellagnech.springlite.di.AnnotationApplicationContext;
import com.bellagnech.springlite.di.ApplicationContext;

public class Application {
    public static void main(String[] args) {
        try {
            // Initialize the context with package to scan
            ApplicationContext context = new AnnotationApplicationContext("com.example");

            // Get beans
            UserController controller = context.getBean("userController", UserController.class);
            OrderService orderService = context.getBean("orderService", OrderService.class);

            // Every request gets a new instance
            RequestHandler handler1 = context.getBean("requestHandler", RequestHandler.class);
            RequestHandler handler2 = context.getBean("requestHandler", RequestHandler.class);

            // handler1 != handler2 (different instances)

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Supported Injection Types

SpringLite supports three types of dependency injection:

### 1. Constructor Injection

- **XML-based**: Use `<property>` elements in bean definition
- **Annotation-based**: Use `@Autowired` on the constructor

Constructor injection is recommended for required dependencies as it enforces their presence at initialization.

### 2. Setter Injection

- **XML-based**: Use `<property>` elements in bean definition
- **Annotation-based**: Use `@Autowired` on setter methods

Setter injection is useful for optional dependencies or when circular dependencies exist.

### 3. Field Injection

- **XML-based**: Use `<property>` elements in bean definition
- **Annotation-based**: Use `@Autowired` directly on fields

Field injection is the simplest approach but makes unit testing more difficult.

## Bean Scopes

SpringLite supports two bean scopes:

- **Singleton**: Default scope. Only one instance is created per context.
- **Prototype**: A new instance is created each time the bean is requested.

## Error Handling

SpringLite provides clear error messages for common issues:

- `NoSuchBeanDefinitionException`: When a bean with the given ID doesn't exist
- `BeanCreationException`: When a bean cannot be created (e.g., missing class)
- `CircularDependencyException`: When circular dependencies are detected

## Logging

SpringLite includes a simple logging utility that helps diagnose issues:

```java
// Configure logging level
Logger.setLevel(Logger.Level.DEBUG);

// Or disable logging for tests
Logger.disable();
```

## Advanced Features

- **Qualifier Support**: Use `@Qualifier` to disambiguate when multiple beans of the same type exist
- **Type Conversion**: Automatic conversion of string values to the required property types
- **Circular Dependency Detection**: Detects and reports circular dependencies with clear messages

## Example Application

See the `com.bellagnech.springlite.examples` package for a complete working example of both XML and annotation-based configuration.

## Limitations

This framework is for educational purposes and has some limitations:

- No AOP (Aspect-Oriented Programming) support
- Limited validation and error handling compared to Spring
- No property placeholders or environment profiles

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
