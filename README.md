# SpringLite

<p align="center">
  <strong>A small dependency injection container built to understand Spring fundamentals.</strong>
</p>

<p align="center">
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-21-3776AB?style=flat-square&logo=openjdk&logoColor=white" alt="Java 21"></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-build-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven"></a>
  <a href="https://github.com/bellagnech/SpringLite/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-MIT-F0C808?style=flat-square" alt="MIT license"></a>
  <img src="https://img.shields.io/badge/status-educational%20POC-6F42C1?style=flat-square" alt="Educational POC">
</p>

SpringLite is a small Java project inspired by Spring's application context. It makes dependency injection easier to study by keeping the moving parts visible: bean definitions, classpath scanning, object creation, injection, scopes, and errors.

## What it demonstrates

- Annotation-based configuration with `@Component`, `@Autowired`, `@Qualifier`, and `@Scope`
- XML-based configuration with `XmlApplicationContext`
- Constructor, setter, and field injection
- Singleton and prototype scopes
- Type-based autowiring and qualifiers
- Bean lifecycle hooks and circular dependency detection
- Simple logging and clear framework exceptions

## Quick start

Requirements: Java 21 and Maven.

```bash
git clone https://github.com/bellagnech/SpringLite.git
cd SpringLite
./mvnw test
```

The Maven wrapper is included so the project can be built without installing Maven globally. The examples live under `src/main/java/com/bellagnech/springlite/examples`.

## Annotation configuration

Mark classes as components and let the context scan their package:

```java
@Component
public class UserService {
    public String welcome(String name) {
        return "Welcome " + name;
    }
}

@Component
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

```java
ApplicationContext context = new AnnotationApplicationContext(
        "com.bellagnech.springlite.examples");

UserController controller = context.getBean(
        "userController", UserController.class);
```

Use `@Autowired` on a field or one-argument setter when those injection styles are useful for the exercise. Add `@Qualifier("beanName")` when several beans implement the same interface.

## XML configuration

The XML context reads bean definitions from the classpath:

```xml
<beans>
    <bean id="userRepository"
          class="com.bellagnech.springlite.examples.repository.InMemoryUserRepository" />
    <bean id="userService"
          class="com.bellagnech.springlite.examples.service.UserServiceImpl">
        <property name="userRepository" ref="userRepository" />
    </bean>
</beans>
```

```java
ApplicationContext context = new XmlApplicationContext("example-beans.xml");
UserService service = context.getBean("userService", UserService.class);
```

XML properties support bean references and basic string conversion for primitive and wrapper values.

## Scopes and errors

Beans are singletons by default. Annotate a component with `@Scope("prototype")`, or set `scope="prototype"` in XML, to create a new instance for every lookup.

The container reports missing beans with `NoSuchBeanDefinitionException`, creation failures with `BeanCreationException`, and dependency cycles with `CircularDependencyException`.

## Project map

```text
src/main/java/com/bellagnech/springlite/
├── di/          container, contexts, definitions, annotations, and exceptions
└── examples/    small repository, service, and controller examples
src/test/java/   tests for scanning, injection, scopes, lifecycle, and errors
```

## Why this project exists

This is a learning project built while getting familiar with Spring concepts. It is intentionally smaller than Spring and does not aim to replace it. The implementation is a place to experiment with reflection and container design, one feature at a time.

## License

This project is available under the MIT License.
