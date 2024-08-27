本文不仅介绍了单元测试的规范，还结合实际开发案例，演示了如何编写单元测试。我们使用了 JUnit、H2、Surefire 等常用的单元测试工具。如果你希望深入了解这些工具，可以查阅相关资料。本文基于企业内部实际应用的工作流程，通过教程指导你编写符合规范的单元测试，从而提升代码质量和项目的可靠性。

## 单元测试的规范

下面是从 [Alibaba Java 开发手册](https://github.com/alibaba/p3c) 中总结出来的单元测试规范：

1. **AIR 原则**

   好的单元测试必须遵守 AIR 原则：

    - Automatic（自动化）：单元测试应全自动执行，不需要手动干预。
    - Independent（独立性）：每个测试用例都是独立的，不依赖其他测试的结果。
    - Repeatable（可重复）：测试结果应始终如一，无论在什么环境下运行。

2. **测试粒度要小**
   单元测试的粒度要足够小，通常测试到类或方法级别，这样可以精确定位问题。跨类或系统的交互逻辑应该在集成测试中处理。
3. **核心代码必须测试**
   对于关键的业务逻辑和模块，必须编写单元测试，确保其代码在新增或修改后通过所有相关测试。
4. **单元测试代码位置**
   单元测试代码应放在 src/test/java 目录下，而不是和业务代码混在一起，以便源码编译时可以跳过这些测试代码。
5. **代码覆盖率要求**
   单元测试的覆盖率应达到 70% 以上，对于核心模块，语句覆盖率和分支覆盖率应达到 100%。
6. **BCDE 原则**
   编写测试时应遵循 BCDE 原则：
    - Border（边界）：测试边界情况，如循环边界、特殊值等。
    - Correct（正确）：测试正确的输入和期望的输出。
    - Design（设计）：根据设计文档编写测试，确保实现与设计一致。
    - Error（错误）：测试错误输入或异常情况，确保系统能够正确处理。

7. **数据库操作的测试**
   不要假设数据库中存在特定数据，测试时要通过代码准备测试数据。手动插入的数据可能导致测试不可靠。

8. **数据库测试的清理机制**
   数据库相关的测试应设定自动回滚机制，避免测试数据污染数据库，或者使用有特殊前缀的标识来区分测试数据。

9. **代码可测试性**
   对于难以测试的代码，考虑进行重构，使代码更容易进行单元测试，避免为了测试而写出不规范的代码。

10. **确定测试范围**
    在设计评审阶段，开发人员应与测试人员一起确定单元测试的覆盖范围，确保所有重要的用例都被覆盖。
11. **单元测试应在项目发布前完成**
    单元测试作为质量保障的手段，应在项目提测前完成，而不是项目发布后再补充。
12. **编写可测代码**
    为了便于测试，业务代码中应避免构造方法过于复杂、全局变量过多、外部依赖过多、条件语句过多等问题。条件语句可以通过重构来简化。
13. **认识单元测试的重要性**
    单元测试不是测试人员的职责，而是开发人员的责任。单元测试代码也是需要维护的，好的单元测试可以有效减少线上故障的发生。

通过以上总结，你可以更好地理解和应用单元测试的规范，从而编写出高质量的、可维护的测试代码，确保项目的稳定性和可靠性。**特别是单元测试往往会与 CI/CD 结合，例如每次在 Githu 申请 PR 后，都会通过 Github Action 执行单元测试，确保合并代码的质量。**



## 单元测试实践

### 目录结构

下面是目录结构，在 test 文件夹中编写单元测试代码，每个单元测试以 Test 结尾。你会发现单元测试文件和代码文件一一对应。

```
demo-ut
├── src
│   ├── main
│   │   ├── java
│   │   │   └── cn
│   │   │       └── tangrl
│   │   │           └── ut
│   │   │               ├── UtApplication.java
│   │   │               ├── controller
│   │   │               │   └── UserController.java
│   │   │               ├── model
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   └── UserRepository.java
│   │   │               └── service
│   │   │                   └── UserService.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       ├── java
│       │   └── cn
│       │       └── tangrl
│       │           └── ut
│       │               ├── controller
│       │               │   └── UserControllerTest.java
│       │               ├── model
│       │               │   └── UserTest.java
│       │               ├── repository
│       │               │   └── UserRepositoryTest.java
│       │               └── service
│       │                   └── UserServiceTest.java
│       └── resources
├── pom.xml
```

### 代码实现

下面是 pom.xml 文件的内容。

**我们使用的是 SpringBoot 3.1.12 和 JDK 21。我们使用 MySQL 作为数据库，使用 Spring JPA 作为 ORM。**

**在 maven 编译的 test 阶段，会使用 h2 内存数据库来代替 MySQL。**

**在 maven 编译的 build 阶段，会使用 maven-surefire-plugin 来控制单元测试的执行并在 target 文件夹中生成报告。**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

  <!-- 模型版本 -->
  <modelVersion>4.0.0</modelVersion>

  <!-- 父项目配置，继承 Spring Boot 的父项目 -->
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.1.12</version>
    <relativePath/> <!-- 从仓库中查找父项目 -->
  </parent>

  <!-- 项目基础信息 -->
  <groupId>cn.tangrl</groupId> <!-- 项目的组 ID -->
  <artifactId>ut</artifactId> <!-- 项目的 artifact ID -->
  <version>0.0.1-SNAPSHOT</version> <!-- 项目的版本号 -->
  <packaging>jar</packaging> <!-- 打包类型 -->
  <name>demo-ut</name> <!-- 项目名称 -->
  <description>demo-ut</description> <!-- 项目描述 -->

  <!-- 项目属性配置 -->
  <properties>
    <java.version>21</java.version> <!-- 指定使用的 Java 版本 -->
  </properties>

  <!-- 项目依赖配置 -->
  <dependencies>
    <!-- Spring Boot Starter Web: 提供构建 Web 应用所需的基本依赖 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Validation: 提供 Bean Validation 支持 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA: 提供 JPA 持久化支持 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Connector: MySQL 数据库驱动 -->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.33</version>
    </dependency>

    <!-- H2 Database: 测试时使用的内存数据库 -->
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>test</scope>
    </dependency>

    <!-- Spring Boot Starter Test: 包含 JUnit 5 和 Mockito，提供测试支持 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
      <!-- 排除 JUnit 4 的支持 (Vintage 引擎) -->
      <exclusions>
        <exclusion>
          <groupId>org.junit.vintage</groupId>
          <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
      </exclusions>
    </dependency>

    <!-- Lombok: 用于减少样板代码 -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>
  </dependencies>

  <!-- 构建配置 -->
  <build>
    <plugins>
      <!-- Spring Boot Maven 插件: 提供 Spring Boot 应用打包支持 -->
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>

      <!-- Maven Surefire 插件: 用于运行单元测试 -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.3.1</version>
      </plugin>
    </plugins>
  </build>
</project>
```

下面是 application.properties 的内容，用于配置 MySQL 和 JPA。如果想要运行代码，不仅仅是跑测试的话，需要创建对应的 test_db 数据库。如果仅仅跑测试的话，不需要使用的 MySQL，在测试的使用使用 H2 内存数据库。

```properties
# MySQL 数据库连接配置
# 数据库连接 URL，指向本地的 MySQL 数据库 'test_db'
spring.datasource.url=jdbc:mysql://localhost:3306/test_db
# 数据库用户名
spring.datasource.username=root
# 数据库密码
spring.datasource.password=root
# MySQL 数据库驱动类
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA 相关配置
# 自动更新数据库模式，选择 'update' 会在应用启动时自动更新数据库结构
spring.jpa.hibernate.ddl-auto=update
# 是否在控制台显示 SQL 语句，'true' 表示显示执行的 SQL 语句
spring.jpa.show-sql=true
```

1. **创建实体类 `User`**

实现一个自定义函数，为字段添加相应的限制。

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    // 简单的业务逻辑：验证电子邮件格式
    public boolean isValidEmail() {
        return this.email != null && this.email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

2. **创建 Repository 接口 `UserRepository`**

实现一个自定义的数据库操作方法。

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 自定义查询方法：根据用户名查找用户
    @Query("SELECT u FROM User u WHERE u.name = :name")
    List<User> findByName(@Param("name") String name);
}
```

3. **创建 Service 层 `UserService`**

实现一个 service 方法，供 controller 接口使用。

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 根据姓名查找用户
    public List<User> findUsersByName(String name) {
        return userRepository.findByName(name);
    }

}
```

4. 创建 Controller 层 `UserController`

实现一个根据姓名查找用户的接口。

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据姓名查找用户
    @GetMapping("/name/{name}")
    public List<User> getUsersByName(@PathVariable String name) {
        return userService.findUsersByName(name);
    }
}
```

### 编写单元测试

下面着重介绍如何实现每个代码文件的单元测试。

1. **测试 `User` 实体类**

**因为我们实现的实体类，包括自定义方法和相关限制，所以需要对其进行相关的测试。否则一般简单的实体类不需要测试。**

下面代码通过 JUnit 5 对 `User` 类的邮箱格式验证和名称字段进行了单元测试，验证了在不同情况下（如有效或无效邮箱、空白或非空名称）类的方法是否表现正确。这些测试确保了 `User` 类在处理用户输入时的核心逻辑是可靠的。

```java
public class UserTest {

    @Test
    void testValidEmail() {
        // 测试有效的邮箱地址
        User user = new User(null, "John Doe", "john.doe@example.com");
        assertTrue(user.isValidEmail(), "Email should be valid");
    }

    @Test
    void testInvalidEmail() {
        // 测试无效的邮箱地址
        User user = new User(null, "John Doe", "john.doeexample.com");
        assertFalse(user.isValidEmail(), "Email should be invalid");
    }

    @Test
    void testBlankName() {
        // 测试空白名称的情况
        User user = new User(null, "", "john.doe@example.com");
        assertTrue(user.getName().isEmpty(), "Name should be empty");
    }

    @Test
    void testNotBlankName() {
        // 测试非空名称的情况
        User user = new User(null, "John Doe", "john.doe@example.com");
        assertFalse(user.getName().isEmpty(), "Name should not be empty");
    }
}
```

2. **测试 `UserRepository`**

**因为我们在 repository 中实现了自定义的数据库操作方法，所以需要对其进行测试。否则，repository 不需要进行测试。**

下面代码是 `UserRepository` 的单元测试，使用 JPA 测试环境在内存数据库（H2）中验证 `findByName` 方法的功能。通过设置动态数据库配置，代码首先保存了两个用户对象，然后分别查找名为 "John Doe" 和 "Jane Doe" 的用户，并验证返回的用户列表是否正确。这种测试确保了 `UserRepository` 在处理数据库查询时的正确性和一致性。

```java
@DataJpaTest  // 使用此注解，进行 JPA 相关的测试，优先加载 h2 内存数据库
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository 进行测试

    @DynamicPropertySource  // 动态设置测试环境下的数据库相关属性，设置使用 h2 内存数据库
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.url",
            () -> "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=true");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.H2Dialect");
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

        // Assert - 验证查询结果
        assertEquals(1, johns.size(), "Should find one John Doe");
        assertEquals("John Doe", johns.get(0).getName());

        assertEquals(1, janes.size(), "Should find one Jane Doe");
        assertEquals("Jane Doe", janes.get(0).getName());
    }
}
```

3. **测试 `UserService`**

**service 中的方法是必须要测试的。**

下面代码是 `UserService` 类的单元测试，使用了 Mockito 来模拟 `UserRepository` 的依赖。测试中首先设置了模拟的行为，让 `userRepository.findByName("John Doe")` 返回一个包含 `John Doe` 用户的列表。然后调用 `UserService` 的 `findUsersByName` 方法，并验证返回的用户列表是否正确，确保返回的用户数量和名称符合预期。此外，测试还验证了 `findByName` 方法在 `UserRepository` 中是否被正确调用了一次。通过这种方式，测试确保了 `UserService` 的逻辑在没有真实数据库依赖的情况下也能被验证。

```java
public class UserServiceTest {

    @InjectMocks
    private UserService userService;  // 注入 UserService 实例用于测试

    @Mock
    private UserRepository userRepository;  // 模拟 UserRepository 依赖

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // 初始化 Mockito 注解
    }

    @Test
    void testFindUsersByName() {
        // Arrange - 设置测试数据和模拟行为
        User user = new User(1L, "John Doe", "john.doe@example.com");
        when(userRepository.findByName("John Doe")).thenReturn(List.of(user));

        // Act - 调用被测试的方法
        List<User> users = userService.findUsersByName("John Doe");

        // Assert - 验证结果和方法调用次数
        assertEquals(1, users.size(), "Should return one user");
        assertEquals("John Doe", users.get(0).getName());
        verify(userRepository, times(1)).findByName("John Doe");
    }
}
```

4. **测试 `UserController`**

**controller 中的接口也是必须要测试的。**

下述代码是对 `UserController` 类的单元测试，使用了 Spring 的 `@WebMvcTest` 注解来测试 Web 层的行为。通过 `MockMvc` 模拟 HTTP 请求，并使用 `@MockBean` 来模拟 `UserService` 的依赖。测试中设置了模拟行为，使得当请求 `UserService.findUsersByName("John Doe")` 时返回一个包含 "John Doe" 的用户列表。然后，通过 `MockMvc` 模拟发送 GET 请求到 `/api/users/name/John Doe`，并验证返回的状态码为 200 OK，同时检查响应的 JSON 数据中是否正确包含用户的名称和邮箱地址。这种测试方式确保了 `UserController` 在处理 HTTP 请求时的行为正确性。

```java
@WebMvcTest(UserController.class)  // 仅加载与 UserController 相关的 Web 层组件进行测试
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 注入 MockMvc 用于模拟 HTTP 请求

    @MockBean
    private UserService userService;  // 模拟 UserService 依赖

    @Test
    void testGetUsersByName() throws Exception {
        // Arrange - 设置测试数据和模拟行为
        User user = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.findUsersByName("John Doe")).thenReturn(List.of(user));

        // Act & Assert - 模拟 GET 请求并验证响应
        mockMvc.perform(get("/api/users/name/John Doe"))
            .andExpect(status().isOk())  // 期望状态码为 200 OK
            .andExpect(jsonPath("$[0].name").value("John Doe"))  // 验证 JSON 响应中的 name 字段
            .andExpect(
                jsonPath("$[0].email").value("john.doe@example.com"));  // 验证 JSON 响应中的 email 字段
    }
}
```

### 运行单元测试

运行单元测试的方法有下面几种：

1. **通过 IDE 运行**：右键点击测试类或方法，然后选择 "Run" 或 "Debug" 选项来执行测试。IDE 通常会提供一个测试结果窗口，显示测试通过、失败或被忽略的详细信息。
2. **通过构建工具运行**：例如 `mvn test`。
3. **在 CI/CD 环境中自动运行**：在持续集成/持续交付（CI/CD）管道中，测试通常会在每次代码提交后自动运行。CI/CD 工具（如 Jenkins、GitLab CI、Travis CI）会在构建过程中执行测试，并根据测试结果决定是否继续后续步骤。