# SpringLite - Mini Dependency Injection Framework

SpringLite is a lightweight dependency injection framework inspired by Spring Framework. This project aims to implement the core features of dependency injection to understand how frameworks like Spring work under the hood.

## Goals

- Create a simple yet functional dependency injection container
- Support XML-based configuration
- Support annotation-based configuration
- Support different types of dependency injection:
  - Constructor injection
  - Setter injection
  - Field injection
- Handle bean lifecycles (singleton and prototype scopes)
- Provide clear error messages for common issues

## Features

- Bean definition and registration
- Bean retrieval via BeanFactory
- Application context for managing the bean container
- XML configuration parsing
- Annotation-based bean discovery
- Various injection mechanisms

## XML-Based Configuration

XML-based configuration allows you to define beans and their dependencies in XML files:

```xml
<beans>
    <!-- Simple bean definition -->
    <bean id="myBean" class="com.example.MyBean" />

    <!-- Bean with property injection -->
    <bean id="beanWithDependency" class="com.example.DependentBean">
        <property name="dependency" ref="myBean" />
    </bean>

    <!-- Bean with value injection -->
    <bean id="valueBean" class="com.example.ValueBean">
        <property name="stringValue" value="Hello, SpringLite!" />
        <property name="intValue" value="42" />
    </property>

    <!-- Bean with prototype scope -->
    <bean id="prototypeBean" class="com.example.PrototypeBean" scope="prototype" />
</beans>
```

## Annotation-Based Configuration

SpringLite supports annotation-based configuration for a more modern approach:

### 1. Component Scanning

First, define your application context to scan specific packages:

```java
ApplicationContext context = new AnnotationApplicationContext("com.example");
```

### 2. Component Definition

Mark your classes as components to be auto-detected:

```java
@Component
public class UserService {
    // Implementation
}

// Specify a custom bean name
@Component("authService")
public class AuthenticationService {
    // Implementation
}

// Create a prototype-scoped bean
@Component
@Scope("prototype")
public class RequestHandler {
    // A new instance will be created for each request
}
```

### 3. Dependency Injection

There are three ways to inject dependencies:

#### Constructor Injection

```java
@Component
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

#### Setter Injection

```java
@Component
public class ProductService {
    private InventoryService inventoryService;

    @Autowired
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
}
```

#### Field Injection

```java
@Component
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;
}
```

### 4. Qualifying Dependencies

When multiple implementations of a type exist, use @Qualifier to disambiguate:

```java
@Component
public class NotificationService {
    @Autowired
    @Qualifier("emailSender")
    private MessageSender messageSender;
}
```

## Error Handling

SpringLite provides clear error messages for common issues:

- Missing dependencies
- Circular dependencies
- No suitable constructor
- Ambiguous autowiring

## Complete Example

Here's a complete example of using SpringLite with annotations:

```java
// Define your components
@Component
public class MessageService {
    public String getMessage() {
        return "Hello from MessageService";
    }
}

@Component
public class UserService {
    private final MessageService messageService;

    @Autowired
    public UserService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String getWelcomeMessage(String username) {
        return messageService.getMessage() + " - Welcome, " + username + "!";
    }
}

// Use the application context
public class Application {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationApplicationContext("com.example");

        UserService userService = (UserService) context.getBean("userService");
        System.out.println(userService.getWelcomeMessage("John"));
    }
}
```

## Project Structure

The core components are:

- `BeanFactory`: Interface for accessing beans
- `ApplicationContext`: Extended interface for configuration
- `BeanDefinition`: Class to hold bean metadata
- XML support: `XmlApplicationContext` and `XmlBeanDefinitionReader`
- Annotation support: `AnnotationApplicationContext` and annotations package
- Annotations: `@Component`, `@Autowired`, `@Qualifier`, `@Scope`
