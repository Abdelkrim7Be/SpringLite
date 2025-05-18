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
    </bean>

    <!-- Bean with prototype scope -->
    <bean id="prototypeBean" class="com.example.PrototypeBean" scope="prototype" />
</beans>
```

## Annotation-Based Configuration

SpringLite also supports annotation-based configuration:

1. Mark your beans with `@Component`:

```java
@Component
public class MyService {
    // Bean implementation
}
```

2. Inject dependencies using `@Autowired`:

```java
@Component
public class MyController {
    private final MyService service;

    // Constructor injection
    @Autowired
    public MyController(MyService service) {
        this.service = service;
    }

    // OR field injection
    @Autowired
    private MyService service;

    // OR setter injection
    @Autowired
    public void setService(MyService service) {
        this.service = service;
    }
}
```

3. Use `@Qualifier` when multiple beans of the same type exist:

```java
@Component
public class MyController {
    @Autowired
    @Qualifier("specificServiceImpl")
    private MyService service;
}
```

4. Specify scope with `@Scope`:

```java
@Component
@Scope("prototype")
public class PrototypeBean {
    // This bean will be instantiated every time it's requested
}
```

## Usage

To use the SpringLite framework in your application:

```java
// For XML-based configuration
ApplicationContext context = new XmlApplicationContext("classpath:beans.xml");

// For annotation-based configuration (coming soon)
ApplicationContext context = new AnnotationApplicationContext("com.example.package");

// Get a bean from the context
MyBean myBean = (MyBean) context.getBean("myBean");
```

More details coming soon as the project progresses.

## Project Structure

The core components are:

- `BeanFactory`: Interface for accessing beans
- `ApplicationContext`: Extended interface for configuration
- `BeanDefinition`: Class to hold bean metadata
- Annotations: `@Component`, `@Autowired`, `@Qualifier`, `@Scope`
