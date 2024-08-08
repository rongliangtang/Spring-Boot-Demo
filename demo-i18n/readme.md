## 什么是国际化
国际化就是让应用适配不同的国家语言。例如在美国显示英文，在中国显示中文。这样可以为不同国家的用户提供更好的体验。
下面介绍前后端分离项目中实现国际化的方案。先了解整体方案，然后介绍不同方案中前后端是如何实现的。

## 实现国际化的方案
下面将常用的四种方案总结成一张表格。不同方案适用于不同的场景。

![](https://blog-1259405505.cos.ap-guangzhou.myqcloud.com/20240808000647.png)

### 纯前端实现
在前端应用中使用语言包文件、动态加载或客户端存储实现多语言支持。通过加载相应的语言资源来根据用户偏好显示不同语言的内容。
适用于单页面应用（SPA）、需要实时切换语言的应用以及主要由静态内容构成的应用。
### 纯后端实现
在服务器端使用模板引擎渲染多语言内容或通过 API 返回多语言内容。服务器根据请求的语言参数生成相应的语言页面。
适用于内容管理系统（CMS）、需要 SEO 优化的应用和需要集中管理和动态更新内容的应用。
### 混合实现
前端处理静态内容的语言切换，后端处理动态内容的国际化。前端和后端共同协作，确保内容的多语言支持。
适用于复杂的大型应用、需要快速响应和动态内容更新的应用，以及前后端密切协作的项目。
### 第三方服务
通过调用第三方翻译服务 API 实现多语言支持，将需要翻译的文本发送给外部服务，由其返回翻译结果。
适用于需要快速多语言支持的小型项目、内容变动不频繁的应用和不具备专业翻译团队或资源的项目。

## 混合实现方案中的后端实现
开发大型的复杂应用，往往使用的是混合实现方案。这个方案中前端处理静态内容的语言切换，例如网页上固定元素的固定内容。后端处理异常消息等动态内容的国际化。
异常国际化允许我们根据用户的语言环境以不同的语言呈现错误信息。通过支持多种语言，我们可以为全球用户提供更好的用户体验。不再仅仅使用单一语言显示错误信息，而是根据用户的首选语言动态翻译，使用户更容易理解和解决问题。
下面介绍后端如何实现异常消息的国际化。
### 开发环境
1. Spring Boot 3.1.12
2. JDK 21
3. IDEA 2024.1.4
### 实现思路
1. 自定义响应码和异常。
2. 自定义响应注解。
3. 编写国际化资源文件，自定义消息和校验消息。
4. 根据请求头header的参数 "Accept-Language" 传递的语言设置Locale。
5. 编写全局异常处理类，处理各种异常及返回错误信息。
6. 编写测试接口。
### 实现步骤
#### 自定义响应码和异常

```java
public enum ResultCode {
    SUCCESS(200, "success", "success"),
    ERROR(500, "error", "error"),
    BUSINESS_EXCEPTION(502, "business.exception", "business exception");

    @Getter
    private final int code;

    @Getter
    private final String messageKey;

    @Getter
    private final String defaultMessage;

    ResultCode(int code, String messageKey, String defaultMessage) {
        this.code = code;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }
}
```

```java
/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class BusinessException extends RuntimeException {

    /**
     * 响应码
     */
    private final ResultCode resultCode;

    /**
     * 构造函数
     *
     * @param resultCode 响应码
     */
    public BusinessException(ResultCode resultCode) {
        // 使用ResultCode的名字作为异常消息
        super(resultCode.getDefaultMessage());
        this.resultCode = resultCode;
    }

    public int getCode() {
        return resultCode.getCode();
    }
}
```

上述代码定义了一个用于表示结果状态的枚举 `ResultCode` 和一个自定义异常 `BusinessException`。

`ResultCode` 枚举包含了响应码、消息键和默认消息，用于描述不同的结果状态。`messageKey` 用来确定在国际化资源文件中的异常消息。`defaultMessage`  用来存放默认的消息，用来传递给自定义异常的父类，

`BusinessException` 类是一个业务逻辑异常，通过构造函数接收一个 `ResultCode` 实例，并将其`defaultMessage`作为父类的异常消息，便于调试。

#### 自定义响应注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface I18nApiException {

    ResultCode value();
}
```

上述代码定义了一个自定义注解 `I18nApiException`。

该注解用于标记接口，表示该接口在处理过程中可能会抛出带有国际化信息的业务异常。

**设计这个注解的作用是，拦截接口运行过程中抛出的非自定义异常，返回指定的异常消息给前端，从而保证异常消息的可读性。在下面会介绍异常拦截是如何实现的。**

注解使用 `@Retention(RetentionPolicy.RUNTIME)` 表示该注解在运行时可用，`@Target(ElementType.METHOD)` 表示该注解只能应用于方法。注解包含一个属性 `value()`，其类型为 `ResultCode` 枚举，用于指定与该方法相关的结果状态和国际化消息。

#### 编写国际化资源文件

```
├── src
│   ├── java
│   └── resources
│       ├── ValidationMessages.properties
│       ├── ValidationMessages_en.properties
│       ├── ValidationMessages_zh.properties
│       ├── application.properties
│       └── i18n
│           ├── messages.properties
│           ├── messages_en.properties
│           └── messages_zh.properties
```

如上述目录结构所示，messages 为自定义消息的国际化资源文件。ValidationMessages 为验证异常消息的国际化资源文件。

<img src="https://blog-1259405505.cos.ap-guangzhou.myqcloud.com/6610176111.png" style="zoom:30%;" />

下面是 messages_zh.properties 文件中的内容。

```
success=成功
error=错误
business.exception=业务逻辑异常
```

注意还需要在配置文件 application.properties 中指定国际化资源文件的位置。ValidationMessages 会自动检测，所以只需要指定 messages 的位置。

```
spring.messages.basename=i18n/messages
spring.messages.encoding=UTF-8
```

#### 设置Locale

```java
public class I18NLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        //获取请求中的语言参数
        String language = httpServletRequest.getHeader("Accept-Language");
        Locale locale;
        if (StringUtils.hasText(language)) {
            // 如果请求头中携带了国际化的参数，创建对应的 Locale 对象
            locale = new Locale(language);
        } else {
            //如果没有，使用默认的 Locale 对象（根据主机的语言环境生成一个 Locale ）。
            locale = Locale.getDefault();
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Locale locale) {

    }
}
```

上述代码定义了一个自定义的 `LocaleResolver` 实现类 `I18NLocaleResolver`。该类用于从 HTTP 请求头中解析语言参数并确定用户的区域设置（Locale）。在 `resolveLocale` 方法中，如果请求头中包含 `Accept-Language` 参数，则根据该参数创建相应的 `Locale` 对象；否则，使用默认的 `Locale`。`setLocale` 方法为空实现，用于满足接口要求但未进行实际操作。这种设计方式允许根据请求头动态确定应用程序的区域设置，从而实现国际化支持。

```java
@Component
public class MessageUtil {

    private static MessageSource messageSource;

    public static String getMessage(String messageKey, String... dynamicValues) {
        return messageSource.getMessage(messageKey, dynamicValues, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String messageKey, Locale locale, String... dynamicValues) {
        return messageSource.getMessage(messageKey, dynamicValues, locale);
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource1) {
        messageSource = messageSource1;
    }
}
```

上述代码定义了一个 `MessageUtil` 工具类，用于获取国际化消息。类中的 `getMessage` 方法根据 `messageKey` 和动态参数，使用 `MessageSource` 从资源文件中获取对应的国际化消息，支持默认语言环境和指定语言环境。`MessageSource` 实例通过 `@Autowired` 注入，并赋值给静态变量 `messageSource`，以便在静态方法中使用。这种设计使得在应用中可以方便地获取和格式化国际化消息。

#### 全局异常处理类

```java
@Slf4j
@RestControllerAdvice
@Order(1)
public class I18nExceptionHandlerAdvice {

    private final MessageUtil messageUtil;

    public I18nExceptionHandlerAdvice(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    /**
     * 处理自定义业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleServiceException(BusinessException e) {
        log.error("Handler Exception: ", e);
        ResultCode resultCode = e.getResultCode();
        String localizedMessage = MessageUtil.getMessage(resultCode.getMessageKey());
        return Result.error(localizedMessage);
    }

    /**
     * 处理注解的异常，用来兜底，定义未定义的异常消息，返回给前端
     * @param e
     * @param method
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception e, HandlerMethod method) {
        I18nApiException annotation = method.getMethodAnnotation(I18nApiException.class);
        log.error("Handler Exception: ", e);
        ResultCode resultCode = annotation.value();
        String localizedMessage = MessageUtil.getMessage(resultCode.getMessageKey());
        return Result.error(localizedMessage);
    }
}
```

上述代码定义了一个 `I18nExceptionHandlerAdvice` 类，用于处理全局异常，特别是带有国际化信息的业务异常。该类通过 `@RestControllerAdvice` 注解实现全局异常处理，并指定优先级 `@Order(1)`。它包含三个异常处理方法：

1. `handleServiceException`：处理自定义的 `BusinessException`，从异常中获取 `ResultCode`，通过 `MessageUtil` 获取对应的国际化消息，并返回包含该消息的 `Result` 对象。
2. `MethodArgumentNotValidException`：通过 `@ExceptionHandler` 注解捕获，调用 `handleMethodArgumentNotValidException` 方法处理，将所有验证错误信息收集为字符串并返回。
3. `exceptionHandler`：处理所有其他非自定义异常，通过检查方法上的 `I18nApiException` 注解获取 `ResultCode`，同样通过 `MessageUtil` 获取国际化消息，并返回包含该消息的 `Result` 对象。

这种设计方式确保了应用在遇到异常时能够返回适当的国际化错误信息，提升用户体验。

#### 测试接口

```java
@Data
@NoArgsConstructor
public class UserReq {

    @NotEmpty(message = "{username_not_empty}")
    private String username;
    
    @NotEmpty(message = "{password_not_empty}")
    private String password;
}
```

上述代码定义了一个 `UserReq` 数据传输对象（DTO），用于接收用户请求的数据。该类具有两个属性：`username` 和 `password`，并应用了字段级别的验证注解。

```java
@RestController
@RequestMapping("/test")
public class TestController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/case1")
    public void testCase1() throws Exception {
        throw new BusinessException(ResultCode.BUSINESS_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/case2")
    @I18nApiException(ResultCode.ERROR)
    public void testCase2() throws Exception {
        throw new Exception();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/case3")
    @I18nApiException(ResultCode.ERROR)
    public void login(@Validated @RequestBody UserReq userRequest) {

    }
}
```

上述代码定义了一个 `TestController` 控制器类，包含三个测试端点。

1. **`testCase1`**：在 `/test/case1` 路径上，抛出一个 `BusinessException` 异常，带有 `ResultCode.BUSINESS_EXCEPTION` 结果码。由 `I18nExceptionHandlerAdvice` 中的 `handleServiceException` 方法处理该异常，返回相应的国际化错误消息。
2. **`testCase2`**：在 `/test/case2` 路径上，抛出一个普通的 `Exception`，并使用 `@I18nApiException` 注解标注，指定 `ResultCode.ERROR` 结果码。该异常由 `I18nExceptionHandlerAdvice` 中的 `exceptionHandler` 方法处理，通过注解中的结果码返回相应的国际化错误消息。
3. **`testCase3：`**：处理 POST 请求，接收并验证 `UserReq` 请求体对象。若验证失败，将根据 `UserReq` 类中的注解返回相应的国际化错误消息。该方法同样使用 `@I18nApiException` 注解，指定 `ResultCode.ERROR` 结果码，以处理方法中的异常。

### 效果演示

话不多说，直接上图。

下面是 `/test/case1` 的测试结果，当 Http Header 中 Accept-Language 为 zh 和 en 的请求结果。

<img src="https://blog-1259405505.cos.ap-guangzhou.myqcloud.com/image.png" style="zoom:30%;" />

<img src="https://blog-1259405505.cos.ap-guangzhou.myqcloud.com/202408080015340.png" style="zoom:30%;" />

下面是 `/test/case2` 的测试结果，当 Http Header 中 Accept-Language 为 zh 的请求结果。**抛出的异常为非自定义异常，返回注解指定的异常消息。**

<img src="https://blog-1259405505.cos.ap-guangzhou.myqcloud.com/output2.png" style="zoom:30%;" />

下面是 `/test/case3` 的测试结果，当 Http Header 中 Accept-Language 为 zh 和请求 body 为空时的请求结果。

<img src="https://blog-1259405505.cos.ap-guangzhou.myqcloud.com/output1.png" style="zoom:30%;" />

## 总结

本文分析了前后端分离项目中实现国际化的方案，并对其中混合实现方案的后端实现进行了详细介绍。

其实 SpringBoot 国际化是由 Spring MVC 的能力做支撑，使用和配置相对简单了。我们只需要做国际化语言的处理即可。对于注解校验的国际化只需去编写国际化资源文件的内容。对于自定义异常需要自己编写异常处理的逻辑，然后从国际化资源文件中获取错误信息返回即可。不管采用何种方式去做国际化，实质基本都是通过不同的Locale获取不同的国际化资源文件里的具体信息。

参考链接：

https://docs.spring.io/spring-boot/reference/features/internationalization.html

https://www.baeldung.com/spring-boot-internationalization

https://medium.com/yildiztech/decoding-i18n-challenges-in-spring-boot-3-exploring-internationalization-895a4ac627df

https://www.bmabk.com/index.php/post/238281.html

https://gitee.com/star95/springboot-i18n.git

