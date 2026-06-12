package cn.javastack.springboot.web.sample;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import cn.javastack.springboot.web.bean.OrderInfo;
import cn.javastack.springboot.web.bean.User;
import cn.javastack.springboot.web.bean.UserXml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * WebMVC 示例能力目录：展示登录、JSON/XML 响应、参数校验、全局异常处理等能力。
 * 微信公众号：Java技术栈
 */
@Slf4j
@Component
public class WebMvcSampleSamples {

    @SampleDemo(
            id = "webmvc-json-response",
            name = "JSON 响应示例",
            description = "演示 Spring Boot WebMVC 的 JSON 响应能力，使用 Jackson 序列化 Java 对象为 JSON 格式返回。支持 @JsonProperty、@JsonIgnore、@JsonInclude 等注解控制输出。",
            category = "WebMVC",
            module = "spring-boot-webmvc",
            tags = {"JSON", "Jackson", "ResponseBody"},
            configHints = {
                    "默认使用 Jackson 作为 JSON 序列化器",
                    "可通过 @JsonProperty 自定义字段名",
                    "@JsonIgnore 可隐藏敏感字段",
                    "@JsonInclude(NON_NULL) 忽略空值字段"
            },
            params = {
                    @SampleParam(name = "userId", type = "long", required = true,
                            description = "用户 ID", example = "1001")
            }
    )
    public Object jsonResponse(Map<String, Object> params) {
        long userId = params != null && params.containsKey("userId")
                ? Long.parseLong(params.get("userId").toString()) : 1001L;

        User user = new User("Java技术栈", 18);
        user.setId(userId);
        user.setAddress("中国");
        user.setMemo("JSON 响应演示");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例模拟了 GET /user/json/{userId} 的 JSON 响应");
        result.put("响应数据", user);
        result.put("原始接口", "GET /user/json/" + userId);
        return result;
    }

    @SampleDemo(
            id = "webmvc-xml-response",
            name = "XML 响应示例",
            description = "演示 Spring Boot WebMVC 的 XML 响应能力，使用 jackson-dataformat-xml 将 Java 对象序列化为 XML 格式。支持 @JacksonXmlRootElement、@JacksonXmlProperty 等注解。",
            category = "WebMVC",
            module = "spring-boot-webmvc",
            tags = {"XML", "Jackson", "ResponseBody"},
            configHints = {
                    "需要引入 jackson-dataformat-xml 依赖",
                    "使用 @JacksonXmlRootElement 定义根元素名",
                    "@JacksonXmlElementWrapper 控制集合包装方式"
            },
            params = {
                    @SampleParam(name = "userId", type = "String", required = true,
                            description = "用户 ID", example = "U001")
            }
    )
    public Object xmlResponse(Map<String, Object> params) {
        String userId = params != null && params.containsKey("userId")
                ? params.get("userId").toString() : "U001";

        UserXml userXml = new UserXml();
        userXml.setId(userId);
        userXml.setName("R哥");

        List<OrderInfo> orders = new ArrayList<>();
        orders.add(new OrderInfo("123456001", 999, new Date()));
        orders.add(new OrderInfo("123456002", 777, new Date()));
        userXml.setOrderList(orders);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例模拟了 GET /user/xml/{userId} 的 XML 响应");
        result.put("响应数据", userXml);
        result.put("原始接口", "GET /user/xml/" + userId);
        return result;
    }

    @SampleDemo(
            id = "webmvc-login",
            name = "登录与安全示例",
            description = "演示 Spring Boot WebMVC 集成 Spring Security 的登录能力。配置了表单登录、角色权限控制（/test/** 需要 ROLE_TEST），内置了 test（ADMIN+TEST）和 root（ADMIN）两个用户。",
            category = "WebMVC",
            module = "spring-boot-webmvc",
            tags = {"Security", "Login", "Authentication"},
            configHints = {
                    "SecurityConfig 配置了 SecurityFilterChain",
                    "/test/** 路径需要 ROLE_TEST 角色",
                    "其他路径 permitAll",
                    "内置用户: test/test（ADMIN+TEST）, root/root（ADMIN）",
                    "启用了表单登录，禁用了 CSRF"
            }
    )
    public Object loginDemo(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例展示了 Spring Security 的安全配置");
        result.put("登录方式", "表单登录（Form Login）");
        result.put("内置用户", Map.of(
                "test", Map.of("密码", "test", "角色", "ADMIN, TEST"),
                "root", Map.of("密码", "root", "角色", "ADMIN")
        ));
        result.put("权限规则", Map.of(
                "/test/**", "需要 ROLE_TEST 角色",
                "/**", "permitAll（公开访问）"
        ));
        result.put("退出接口", "GET /logout");
        result.put("安全特性", List.of("表单登录", "角色权限控制", "CSRF 禁用"));
        return result;
    }

    @SampleDemo(
            id = "webmvc-validation",
            name = "参数校验示例",
            description = "演示 Spring Boot WebMVC 的参数校验能力。支持 @Validated + @RequestBody 的方法参数校验和 @Size 等方法参数校验，通过 GlobalExceptionHandler 统一处理校验异常。",
            category = "WebMVC",
            module = "spring-boot-webmvc",
            tags = {"Validation", "Validated", "ExceptionHandler"},
            configHints = {
                    "需要引入 spring-boot-starter-validation 依赖",
                    "@Validated 标注在 Controller 类上启用方法参数校验",
                    "@Validated 标注在 @RequestBody 参数上启用请求体校验",
                    "GlobalExceptionHandler 统一处理 MethodArgumentNotValidException 和 ConstraintViolationException"
            },
            params = {
                    @SampleParam(name = "username", type = "String", required = true,
                            description = "用户名（5-10个字符）", example = "javastack"),
                    @SampleParam(name = "age", type = "int", required = true,
                            description = "年龄", example = "25")
            }
    )
    public Object validationDemo(Map<String, Object> params) {
        String username = params != null && params.containsKey("username")
                ? params.get("username").toString() : null;
        Integer age = params != null && params.containsKey("age")
                ? Integer.parseInt(params.get("age").toString()) : null;

        Map<String, Object> result = new LinkedHashMap<>();

        // 模拟校验
        List<String> errors = new ArrayList<>();
        if (username == null || username.isEmpty()) {
            errors.add("username：不能为空");
        } else if (username.length() < 5 || username.length() > 10) {
            errors.add("username：长度必须在 5-10 之间");
        }
        if (age == null) {
            errors.add("age：不能为空");
        }

        result.put("提示", "此示例模拟了 POST /user/save 的参数校验");
        result.put("原始接口", "POST /user/save（使用 @Validated @RequestBody User）");
        if (errors.isEmpty()) {
            User user = new User(username, age);
            user.setId(1L);
            result.put("校验结果", "通过");
            result.put("响应数据", user);
        } else {
            result.put("校验结果", "失败");
            result.put("错误信息", errors);
            result.put("异常类型", "MethodArgumentNotValidException");
        }
        return result;
    }

    @SampleDemo(
            id = "webmvc-global-exception",
            name = "全局异常处理示例",
            description = "演示 Spring Boot WebMVC 的全局异常处理能力。通过 @ControllerAdvice + @ExceptionHandler 统一捕获和处理不同类型的异常，返回友好的错误信息。",
            category = "WebMVC",
            module = "spring-boot-webmvc",
            tags = {"Exception", "ControllerAdvice", "ErrorHandler"},
            configHints = {
                    "GlobalExceptionHandler 使用 @ControllerAdvice 标注",
                    "@ExceptionHandler(Exception.class) 处理通用异常",
                    "@ExceptionHandler(MethodArgumentNotValidException.class) 处理参数校验异常",
                    "@ExceptionHandler(ConstraintViolationException.class) 处理约束校验异常"
            }
    )
    public Object globalExceptionDemo(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例展示了全局异常处理的能力");
        result.put("异常处理器", "GlobalExceptionHandler（@ControllerAdvice）");
        result.put("处理的异常类型", Map.of(
                "Exception", "通用异常，返回 'global exception'",
                "MethodArgumentNotValidException", "请求体参数校验失败，返回具体字段错误信息",
                "ConstraintViolationException", "方法参数约束校验失败，返回约束违反信息"
        ));
        result.put("模拟触发", "访问 GET /user/json/1（userId 长度不满足 @Size(min=5,max=8)）将触发 ConstraintViolationException");
        result.put("自定义错误页面", "ErrorRegister 注册了 400/404/500 错误页面路由到 /error");
        return result;
    }
}
