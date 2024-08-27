在上一篇文章中，我们介绍了如何编写单元测试。这篇文章将更进一步，讲解如何编写集成测试。我们将首先讨论单元测试和集成测试之间的区别，接着介绍集成测试的目录结构，最后展示集成测试的具体实现。

编写集成测试时，主要使用 [Testcontainers](https://testcontainers.com/) 和 [Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/) 这两个工具。Testcontainers 允许在测试过程中启动容器，从而实现端到端的测试，而 Failsafe 则负责在构建阶段控制集成测试的执行。

<!--more-->

## 单元测试和集成测试的区别

**单元测试** 是针对代码中的最小功能单元（通常是单个方法或类）进行的测试，目的是验证这些单元是否按预期工作。单元测试通常通过模拟外部依赖来隔离测试环境。

**集成测试** 则验证多个组件或模块之间的交互是否正确，确保它们能够协同工作。集成测试通常涉及实际的数据库、网络等外部系统，目的是检查模块之间的接口和数据流是否正确。

如果对以上概念仍有些模糊，别担心，接下来我们将通过实际案例来详细说明如何实现集成测试。**本文的集成测试是基于上一篇单元测试教程中的代码进行的。如果你还没有看过上一篇，可以先去了解一下。**



## 集成测试规范

在 Java 集成测试中，遵循以下规范可以帮助确保测试的有效性和可靠性：

1. **真实环境配置**：尽量在与生产环境相似的环境中进行测试，使用真实的数据库、消息队列等，以捕捉集成中的潜在问题。

2. **使用 `@SpringBootTest` 注解**：通过 `@SpringBootTest` 注解启动整个 Spring 应用上下文，确保各个组件之间的依赖关系能够得到正确测试。

3. **事务管理**：使用 `@Transactional` 注解保证测试的数据隔离，测试完成后自动回滚事务，避免对数据库造成污染。

4. **测试覆盖**：覆盖关键的业务流程和模块交互，确保在模块组合后仍然能够满足业务需求。

5. **依赖注入的使用**：通过依赖注入的方式加载真实组件，而不是使用模拟对象（如 Mockito），以确保组件之间的真实交互能够正确工作。

6. **日志和监控**：在测试中开启详细日志和监控，帮助快速定位问题。

7. **数据准备与清理**：使用合适的工具或框架（如 Testcontainers 或 Flyway）进行数据的准备和清理，确保每次测试的数据状态一致。

这些规范有助于在集成测试中确保系统模块之间的正确性和稳定性。



## 集成测试实践

### 目录结构

如下所示的目录结构展示了整个项目的组成。项目主要包括两个模块：`server` 模块和 `integration-test` 模块。

- `server` 模块包含了上一篇文章中的代码，为了使项目结构更为规范，我们将这些代码放在了 `server` 模块中。
- `integration-test` 模块则专门用于存放 `server` 模块中代码的集成测试。

目录结构如下：

```
.
├── integration-test
│   ├── pom.xml
│   ├── src
│      └── test
│          └── java
│              └── cn
│                  └── tangrl
│                      └── server
│                          ├── controller
│                          │   └── UserControllerIT.java
│                          ├── repository
│                          │   └── UserRepositoryIT.java
│                          └── service
│                              └── UserServiceIT.java
│		
└── server
│   ├── pom.xml
│   ├── src
│      ├── main
│      │   ├── java
│      │   │   └── cn
│      │   │       └── tangrl
│      │   │           └── server
│      │   │               ├── ItApplication.java
│      │   │               ├── controller
│      │   │               │   └── UserController.java
│      │   │               ├── model
│      │   │               │   └── User.java
│      │   │               ├── repository
│      │   │               │   └── UserRepository.java
│      │   │               └── service
│      │   │                   └── UserService.java
│      │   └── resources
│      │       └── application.properties
│      └── test
│          ├── java
│          │   └── cn
│          │       └── tangrl
│          │           └── server
│          │               ├── controller
│          │               │   └── UserControllerTest.java
│          │               ├── model
│          │               │   └── UserTest.java
│          │               ├── repository
│          │               │   └── UserRepositoryTest.java
│          │               └── service
│          │                   └── UserServiceTest.java
│          └── resources
├── pom.xml
```

**将集成测试代码放在独立的一个模块是 Apache 项目的常见做法（例如 flink），这样的好处是：**

1. **模块化管理**：集成测试与单元测试、业务逻辑代码分离，模块化管理使项目结构更加清晰，方便维护和理解。
2. **依赖隔离**：集成测试模块可以独立配置自己的依赖，如测试数据库、测试工具等，不会影响其他模块，避免了不必要的冲突。
3. **提高构建效率**：在 CI/CD 流程中，可以选择性地运行集成测试模块，而不影响单元测试或业务逻辑的构建流程，这样可以加快开发反馈周期。
4. **测试环境独立性**：可以为集成测试配置专属的测试环境配置文件，确保测试与生产配置隔离，降低误操作风险。
5. **更好的代码组织**：有助于清晰地组织和管理测试代码，特别是在大型项目中，独立模块的管理使得测试代码易于扩展和维护。

### 编写集成测试

下面是 `Server` 模块中 `pom.xml` 文件的 `build` 部分。为了使 `integration-test` 模块能够引用 `server` 模块的业务代码并进行集成测试，我们需要通过 `repackage` 生成一个可执行的 JAR 文件。默认情况下，Spring Boot 会生成一个包含嵌入式 Tomcat 的可部署 JAR 包，这个包只能用于部署，无法作为依赖引用。因此，我们在 `build` 配置中进行了特殊设置：

```xml
<!-- 构建配置 -->
  <build>
    <plugins>
      <!-- Spring Boot Maven 插件: 提供 Spring Boot 应用打包支持 -->
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <!-- 配置 repackage 目标和 classifier 后缀，生成一个可执行的 JAR 文件，使集成测试模块能够引用 -->
        <executions>
          <execution>
            <id>repackage</id>
            <goals>
              <goal>repackage</goal>
            </goals>
            <configuration>
              <classifier>exec</classifier>
            </configuration>
          </execution>
        </executions>
      </plugin>
      
      <!-- Maven Surefire 插件: 用于运行单元测试 -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.3.1</version>
      </plugin>
    </plugins>
  </build>
```

`integration-test` 模块的 `pom.xml` 文件内容如下。该模块使用了 `Testcontainers` 和 `Failsafe` 这两个工具。`Testcontainers` 可以在测试运行时启动容器，实现端到端的测试。而 `Failsafe` 插件则用于控制集成测试的执行，确保在构建阶段运行集成测试。

**注意**：使用 `Testcontainers` 时，需要确保本地已安装 [Docker](https://www.docker.com/get-started/)。

在 `build` 配置中，`Failsafe` 插件会在 Maven 的 `verify` 阶段执行集成测试并检查测试结果。如果集成测试失败，构建过程将终止，从而保证代码质量。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>cn.tangrl</groupId>
    <artifactId>it</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </parent>

  <artifactId>integration-test</artifactId>

  <properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <dependency>
      <groupId>cn.tangrl</groupId>
      <artifactId>server</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
    <!-- Spring Boot Test -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <scope>test</scope>
    </dependency>
    <!-- Spring Data JPA and MySQL dependencies -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.33</version>
    </dependency>
    <!-- Testcontainers dependencies -->
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>testcontainers</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>mysql</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <!-- Maven Failsafe 插件: 用于运行集成测试 -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.3.1</version>
        <executions>
          <execution>
            <goals>
              <goal>integration-test</goal>
              <goal>verify</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

</project>
```

通过这种配置，`integration-test` 模块可以引用 `server` 模块中的代码，并使用 `Testcontainers` 和 `Failsafe` 进行集成测试，确保各模块的功能能够在一起顺利工作。

1. **测试 repository 代码**

在进行集成测试时，通常不会单独测试 `model` 实体类。实体类的验证通常依赖于上层 `repository` 的集成测试，主要测试实体类的 CRUD 操作。

对于 `repository` 层的测试，重点是测试自定义的方法。

下面的代码展示了 `UserRepository` 类的集成测试。测试中使用 Testcontainers 来模拟真实的 MySQL 数据库环境。通过 `@Container` 注解启动 MySQL 容器，并使用 `@DynamicPropertySource` 动态注入容器的连接信息到 Spring 环境中。测试运行在完整的 Spring Boot 上下文中，`@SpringBootTest` 注解确保加载所有必要的 Bean。在测试方法中，首先将一些测试数据保存到数据库中，然后使用 `findByName` 方法查询用户，并验证查询结果是否正确。这一系列步骤确保了 `UserRepository` 在真实数据库环境中的功能表现是正确的。

```java
@Testcontainers  // 启用 Testcontainers 支持，用于在测试中使用容器化的数据库
@SpringBootTest  // 运行 Spring Boot 上下文，用于集成测试
class UserRepositoryIT {

    @Container  // 定义一个 MySQL 容器，用于在测试过程中模拟真实的 MySQL 数据库
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
        "mysql:8.0.33").withDatabaseName("testdb").withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository 用于测试

    @DynamicPropertySource  // 动态配置测试数据库属性，将容器中的数据库连接信息注入 Spring 的环境配置中
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @BeforeEach
    void setUp() {
        // 可选：在每个测试之前准备测试数据
    }

    @AfterEach
    void tearDown() {
        // 清理数据库中的测试数据，保证测试环境的独立性
        userRepository.deleteAll();
    }

    @Test
    void testFindByName() {
        // Arrange - 设置测试数据
        User user1 = new User(null, "John Doe", "john.doe@example.com");
        User user2 = new User(null, "Jane Doe", "jane.doe@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        // Act - 执行查询操作
        List<User> johns = userRepository.findByName("John Doe");
        List<User> janes = userRepository.findByName("Jane Doe");

        // Assert - 验证查询结果是否符合预期
        assertEquals(1, johns.size(), "Should find one John Doe");
        assertEquals("John Doe", johns.get(0).getName());

        assertEquals(1, janes.size(), "Should find one Jane Doe");
        assertEquals("Jane Doe", janes.get(0).getName());
    }
}
```

2. **测试 service 服务类代码**

下述代码是 `UserService` 类的集成测试，使用 Testcontainers 通过容器化的 MySQL 数据库来模拟真实的数据库环境。测试运行在完整的 Spring Boot 上下文中，通过 `@SpringBootTest` 注解加载所有必要的 Bean。在每次测试之前，通过 `@BeforeEach` 准备数据，在测试完成后通过 `@AfterEach` 清理数据库。测试用例 `testFindUsersByName` 验证了 `UserService` 的 `findUsersByName` 方法，确保它能够正确地根据用户名查询到用户。这种集成测试方式确保了 `UserService` 在实际运行环境中的功能是正确的，并且与数据库的交互正常。

```java
@Testcontainers  // 启用 Testcontainers 支持，用于在测试中使用容器化的数据库
@SpringBootTest  // 运行 Spring Boot 上下文，用于集成测试
public class UserServiceIT {

    @Container  // 定义一个 MySQL 容器，用于在测试过程中模拟真实的 MySQL 数据库
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
        "mysql:8.0.33").withDatabaseName("testdb").withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private UserService userService;  // 注入 UserService 用于测试
    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository 用于操作数据库

    @DynamicPropertySource  // 动态配置测试数据库属性，将容器中的数据库连接信息注入 Spring 的环境配置中
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @BeforeEach
    void setUp() {
        // 在每个测试之前准备数据
    }

    @AfterEach
    void tearDown() {
        // 清理数据库中的数据，确保测试环境的独立性
        userRepository.deleteAll();
    }

    @Test
    void testFindUsersByName() {
        // Arrange - 设置测试数据
        User user1 = new User(null, "John Doe", "john.doe@example.com");
        User user2 = new User(null, "Jane Doe", "jane.doe@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        // Act - 调用 UserService 方法进行查询
        List<User> foundUsers = userService.findUsersByName("John Doe");

        // Assert - 验证查询结果是否正确
        assertEquals(1, foundUsers.size());
        assertEquals("John Doe", foundUsers.get(0).getName());
    }
}
```

3. **测试 controller 接口类代码**

下述代码是对 `UserController` 类的集成测试，使用 `TestRestTemplate` 来模拟实际的 HTTP 请求和响应。测试运行在完整的 Spring Boot 应用上下文中，并通过 `Testcontainers` 提供的 MySQL 容器模拟真实的数据库环境。测试用例验证了 API 在查询存在和不存在的用户时的行为，确保 `UserController` 的 REST API 能够正确返回预期的结果。

```java
@Testcontainers  // 启用 Testcontainers 支持，用于在测试中使用容器化的数据库
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 启动带有随机端口的 Spring Boot 应用，用于集成测试
public class UserControllerIT {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
        "mysql:8.0.33").withDatabaseName("testdb").withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private TestRestTemplate restTemplate;  // 使用 TestRestTemplate 进行 REST 调用

    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository，用于操作数据库

    @Autowired
    private UserService userService;  // 注入 UserService

    @DynamicPropertySource  // 动态配置测试数据库属性，将容器中的数据库连接信息注入 Spring 的环境配置中
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @BeforeEach
        // 每个测试方法执行前的初始化操作
    void setUp() {
        userRepository.deleteAll();  // 清理数据库中的数据，确保测试环境的独立性
        userRepository.save(new User(null, "John Doe", "john.doe@example.com"));  // 添加测试数据
        userRepository.save(new User(null, "Jane Doe", "jane.doe@example.com"));
    }

    @Test
    void testGetUsersByName() {
        // 使用 TestRestTemplate 调用 API 并获取响应
        ResponseEntity<User[]> response = restTemplate.getForEntity("/api/users/name/John Doe",
            User[].class);

        // 验证响应状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证响应内容
        User[] users = response.getBody();
        assertNotNull(users);
        assertEquals(1, users.length);
        assertEquals("John Doe", users[0].getName());
        assertEquals("john.doe@example.com", users[0].getEmail());
    }

    @Test
    void testGetUsersByName_NotFound() {
        // 使用 TestRestTemplate 调用 API 并获取响应
        ResponseEntity<User[]> response = restTemplate.getForEntity(
            "/api/users/name/Nonexistent User", User[].class);

        // 验证响应状态码
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 验证响应内容为空
        User[] users = response.getBody();
        assertNotNull(users);
        assertEquals(0, users.length);
    }
}
```

### 运行集成测试

运行集成测试的方法有下面几种：

1. **通过 IDE 运行**：右键点击测试类或方法，然后选择 "Run" 或 "Debug" 选项来执行测试。IDE 通常会提供一个测试结果窗口，显示测试通过、失败或被忽略的详细信息。
2. **通过构建工具运行**：例如 `mvn verify`。
3. **在 CI/CD 环境中自动运行**：在持续集成/持续交付（CI/CD）管道中，测试通常会在每次代码提交后自动运行。CI/CD 工具（如 Jenkins、GitLab CI、Travis CI）会在构建过程中执行测试，并根据测试结果决定是否继续后续步骤。