package cn.javastack.springboot.web.sample;

import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import cn.javastack.springboot.web.bean.User;
import cn.javastack.springboot.web.controller.ResponseBodyController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WebMVC 模块示例注册
 */
@Component
@RequiredArgsConstructor
public class WebMvcSampleDemos {

    private final ResponseBodyController responseBodyController;

    @SampleDemo(
            id = "webmvc-json-response",
            name = "JSON 响应",
            description = "演示 Spring MVC 返回 JSON 格式数据，使用 Jackson 进行序列化，支持 @JsonProperty、@JsonIgnore、@JsonInclude 等注解",
            category = "Response",
            module = "spring-boot-webmvc",
            params = {
                    @SampleParam(name = "userId", type = "String", required = true,
                            description = "用户ID（5-8个字符）", example = "12345")
            },
            configHints = {"server.port=8443 (SSL) 或 8080 (HTTP)", "produces=application/json"},
            tags = {"json", "jackson", "rest"}
    )
    public Object jsonResponse(Map<String, Object> params) {
        String userId = String.valueOf(params.getOrDefault("userId", "12345"));
        return responseBodyController.getJsonUserInfo(userId);
    }

    @SampleDemo(
            id = "webmvc-xml-response",
            name = "XML 响应",
            description = "演示 Spring MVC 返回 XML 格式数据，使用 Jackson XML 模块，支持 @JacksonXmlRootElement、@JacksonXmlElementWrapper 等注解",
            category = "Response",
            module = "spring-boot-webmvc",
            params = {
                    @SampleParam(name = "userId", type = "String", required = true,
                            description = "用户ID", example = "user001")
            },
            configHints = {"需要 jackson-dataformat-xml 依赖", "produces=application/xml"},
            tags = {"xml", "jackson"}
    )
    public Object xmlResponse(Map<String, Object> params) {
        String userId = String.valueOf(params.getOrDefault("userId", "user001"));
        return responseBodyController.getXmlUserInfo(userId);
    }

    @SampleDemo(
            id = "webmvc-user-save",
            name = "用户保存与参数校验",
            description = "演示 @RequestBody + @Validated 参数校验，使用 @NotNull、@Size 等约束注解，校验失败由 GlobalExceptionHandler 统一处理",
            category = "Validation",
            module = "spring-boot-webmvc",
            params = {
                    @SampleParam(name = "userName", type = "String", required = true,
                            description = "用户名（5-10个字符）", example = "JavaStack"),
                    @SampleParam(name = "age", type = "Integer", required = true,
                            description = "年龄", example = "25")
            },
            configHints = {"需要 spring-boot-starter-validation 依赖", "校验失败返回 MethodArgumentNotValidException"},
            tags = {"validation", "rest"}
    )
    public Object userSaveWithValidation(Map<String, Object> params) {
        String userName = String.valueOf(params.getOrDefault("userName", "JavaStack"));
        Integer age = Integer.valueOf(String.valueOf(params.getOrDefault("age", "25")));
        User user = new User(userName, age);
        return responseBodyController.saveUser(user);
    }

    @SampleDemo(
            id = "webmvc-security",
            name = "Spring Security 登录认证",
            description = "演示 Spring Security 的基于内存的用户认证、角色授权、表单登录和登出配置",
            category = "Security",
            module = "spring-boot-webmvc",
            configHints = {
                    "用户: test/test（角色: ADMIN, TEST）, root/root（角色: ADMIN）",
                    "/test/** 需要 TEST 角色",
                    "其他路径允许匿名访问",
                    "CSRF 已禁用"
            },
            tags = {"security", "login", "authentication"}
    )
    public Object securityDemo(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Spring Security 内存用户认证配置");
        result.put("users", List.of(
                Map.of("username", "test", "password", "test", "roles", List.of("ADMIN", "TEST")),
                Map.of("username", "root", "password", "root", "roles", List.of("ADMIN"))
        ));
        result.put("protectedPaths", Map.of("/test/**", "需要 TEST 角色"));
        result.put("loginType", "formLogin（表单登录）");
        result.put("logoutUrl", "/logout");
        result.put("logoutSuccessUrl", "/");
        result.put("csrfEnabled", false);
        return result;
    }

    @SampleDemo(
            id = "webmvc-exception-handling",
            name = "全局异常处理",
            description = "演示 @ControllerAdvice + @ExceptionHandler 实现全局异常处理，覆盖参数校验异常（MethodArgumentNotValidException、ConstraintViolationException）和通用异常",
            category = "Exception",
            module = "spring-boot-webmvc",
            configHints = {
                    "GlobalExceptionHandler 使用 @ControllerAdvice 全局拦截",
                    "MethodArgumentNotValidException: @RequestBody + @Validated 校验失败",
                    "ConstraintViolationException: 方法参数直接约束校验失败"
            },
            tags = {"exception", "validation", "controlleradvice"}
    )
    public Object globalExceptionHandling(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "@ControllerAdvice 全局异常处理机制");
        result.put("handlerClass", "GlobalExceptionHandler");
        result.put("handledExceptions", List.of(
                Map.of("exception", "MethodArgumentNotValidException",
                        "scenario", "@RequestBody + @Validated 校验失败",
                        "response", "参数校验失败: 字段名：错误信息"),
                Map.of("exception", "ConstraintViolationException",
                        "scenario", "方法参数直接使用 @Size 等约束注解校验失败",
                        "response", "参数校验失败: 属性路径：错误信息"),
                Map.of("exception", "Exception",
                        "scenario", "其他未捕获异常",
                        "response", "global exception")
        ));
        return result;
    }

}
