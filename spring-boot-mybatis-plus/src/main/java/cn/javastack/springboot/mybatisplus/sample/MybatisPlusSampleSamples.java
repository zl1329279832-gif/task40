package cn.javastack.springboot.mybatisplus.sample;

import cn.javastack.springboot.mybatisplus.entity.UserDO;
import cn.javastack.springboot.mybatisplus.service.UserService;
import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MyBatis-Plus 示例能力目录：展示用户增删改查能力。
 * 微信公众号：Java技术栈
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MybatisPlusSampleSamples {

    private final UserService userService;

    @SampleDemo(
            id = "mybatis-plus-get-by-id",
            name = "根据 ID 查询用户",
            description = "演示 MyBatis-Plus 的 BaseMapper.selectById 能力，通过主键查询单条用户记录。对应 GET /users/{id} 接口。",
            category = "MyBatis-Plus",
            module = "spring-boot-mybatis-plus",
            tags = {"CRUD", "Select", "BaseMapper"},
            configHints = {
                    "实体类使用 @TableName(\"t_user\") 映射表名",
                    "@TableId(type = IdType.AUTO) 使用自增主键",
                    "MyBatis-Plus 自动提供 selectById 方法"
            },
            params = {
                    @SampleParam(name = "id", type = "int", required = true,
                            description = "用户 ID", example = "1")
            }
    )
    public Object getById(Map<String, Object> params) {
        int id = params != null && params.containsKey("id")
                ? Integer.parseInt(params.get("id").toString()) : 1;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例执行了 BaseMapper.selectById(" + id + ")");
        result.put("原始接口", "GET /users/" + id);
        try {
            UserDO user = userService.getById(id);
            if (user != null) {
                result.put("查询结果", user);
            } else {
                result.put("查询结果", "未找到 ID 为 " + id + " 的用户");
            }
        } catch (Exception e) {
            result.put("执行异常", e.getMessage());
            result.put("提示", "请确认 MySQL 数据库已启动且 t_user 表存在");
        }
        return result;
    }

    @SampleDemo(
            id = "mybatis-plus-get-by-username",
            name = "根据用户名查询用户",
            description = "演示 MyBatis-Plus 的自定义查询能力。type=0 使用 XML 定义的 SQL 查询，type=1 使用 LambdaQueryWrapper 条件构造器查询。对应 GET /users?username=&type= 接口。",
            category = "MyBatis-Plus",
            module = "spring-boot-mybatis-plus",
            tags = {"CRUD", "Select", "LambdaQueryWrapper", "XML"},
            configHints = {
                    "UserMapper.xml 中定义了 selectByUsername 的 XML SQL",
                    "UserServiceImpl 中 type=0 走 XML 查询，type!=0 走 LambdaQueryWrapper",
                    "LambdaQueryWrapper 使用链式 API 构建 username + status 条件"
            },
            params = {
                    @SampleParam(name = "username", type = "String", required = true,
                            description = "用户名", example = "admin"),
                    @SampleParam(name = "type", type = "int", required = false,
                            description = "查询类型：0=XML查询，1=LambdaQueryWrapper", defaultValue = "0", example = "0")
            }
    )
    public Object getByUsername(Map<String, Object> params) {
        String username = params != null && params.containsKey("username")
                ? params.get("username").toString() : "admin";
        int type = params != null && params.containsKey("type")
                ? Integer.parseInt(params.get("type").toString()) : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例执行了 UserService.getByUsername(\"" + username + "\", " + type + ")");
        result.put("查询方式", type == 0 ? "XML SQL 查询（UserMapper.xml）" : "LambdaQueryWrapper 条件构造器");
        result.put("原始接口", "GET /users?username=" + username + "&type=" + type);
        try {
            UserDO user = userService.getByUsername(username, type);
            if (user != null) {
                result.put("查询结果", user);
            } else {
                result.put("查询结果", "未找到用户名为 \"" + username + "\" 的用户");
            }
        } catch (Exception e) {
            result.put("执行异常", e.getMessage());
            result.put("提示", "请确认 MySQL 数据库已启动且 t_user 表存在");
        }
        return result;
    }

    @SampleDemo(
            id = "mybatis-plus-create-user",
            name = "创建用户",
            description = "演示 MyBatis-Plus 的 IService.save 能力，插入一条新用户记录。支持 @TableField(fill = FieldFill.INSERT) 自动填充 createTime。对应 POST /users 接口。",
            category = "MyBatis-Plus",
            module = "spring-boot-mybatis-plus",
            tags = {"CRUD", "Insert", "IService", "MetaObjectHandler"},
            configHints = {
                    "IService.save() 执行插入操作",
                    "@TableField(fill = FieldFill.INSERT) 配合 CustomMetaObjectHandler 自动填充 createTime",
                    "@TableField(insertStrategy = FieldStrategy.NOT_NULL) 仅非空字段参与 INSERT"
            },
            params = {
                    @SampleParam(name = "username", type = "String", required = true,
                            description = "用户名", example = "testuser"),
                    @SampleParam(name = "phone", type = "String", required = false,
                            description = "手机号", example = "13800138000"),
                    @SampleParam(name = "status", type = "int", required = false,
                            description = "状态（1=正常）", defaultValue = "1", example = "1")
            }
    )
    public Object createUser(Map<String, Object> params) {
        String username = params != null && params.containsKey("username")
                ? params.get("username").toString() : "testuser";
        String phone = params != null && params.containsKey("phone")
                ? params.get("phone").toString() : null;
        int status = params != null && params.containsKey("status")
                ? Integer.parseInt(params.get("status").toString()) : 1;

        UserDO user = new UserDO();
        user.setUsername(username);
        user.setPhone(phone);
        user.setStatus(status);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例执行了 UserService.save(user)");
        result.put("原始接口", "POST /users（请求体为 UserDO JSON）");
        try {
            userService.save(user);
            result.put("执行结果", "插入成功");
            result.put("新用户", user);
        } catch (Exception e) {
            result.put("执行异常", e.getMessage());
            result.put("提示", "请确认 MySQL 数据库已启动且 t_user 表存在");
        }
        return result;
    }

    @SampleDemo(
            id = "mybatis-plus-list-users",
            name = "查询全部用户",
            description = "演示 MyBatis-Plus 的 IService.list 能力，查询 t_user 表中的所有用户记录。",
            category = "MyBatis-Plus",
            module = "spring-boot-mybatis-plus",
            tags = {"CRUD", "Select", "List", "IService"},
            configHints = {
                    "IService.list() 查询全表数据",
                    "可通过 LambdaQueryWrapper 添加过滤条件"
            }
    )
    public Object listUsers(Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("提示", "此示例执行了 UserService.list()");
        try {
            List<UserDO> users = userService.list();
            result.put("查询结果", users);
            result.put("记录数", users.size());
        } catch (Exception e) {
            result.put("执行异常", e.getMessage());
            result.put("提示", "请确认 MySQL 数据库已启动且 t_user 表存在");
        }
        return result;
    }
}
