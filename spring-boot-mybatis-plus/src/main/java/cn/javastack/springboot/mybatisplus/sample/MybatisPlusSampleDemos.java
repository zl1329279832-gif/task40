package cn.javastack.springboot.mybatisplus.sample;

import cn.javastack.springboot.mybatisplus.entity.UserDO;
import cn.javastack.springboot.mybatisplus.service.UserService;
import cn.javastack.springboot.sample.api.annotation.SampleDemo;
import cn.javastack.springboot.sample.api.annotation.SampleParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MyBatis-Plus 模块示例注册
 */
@Component
@RequiredArgsConstructor
public class MybatisPlusSampleDemos {

    private final UserService userService;

    @SampleDemo(
            id = "mybatis-add-user",
            name = "新增用户",
            description = "演示 MyBatis-Plus 的 IService.save() 方法新增用户，createTime 通过 MetaObjectHandler 自动填充",
            category = "CRUD",
            module = "spring-boot-mybatis-plus",
            params = {
                    @SampleParam(name = "username", type = "String", required = true,
                            description = "用户名", example = "testuser"),
                    @SampleParam(name = "phone", type = "String", required = true,
                            description = "手机号", example = "13800138000")
            },
            configHints = {
                    "需要 MySQL 数据库: localhost:3306/javastack",
                    "表结构: t_user(id, username, phone, create_time, status)",
                    "createTime 由 CustomMetaObjectHandler 自动填充"
            },
            tags = {"mybatis-plus", "crud", "save"}
    )
    public Object addUser(Map<String, Object> params) {
        UserDO user = new UserDO();
        user.setUsername(String.valueOf(params.getOrDefault("username", "testuser")));
        user.setPhone(String.valueOf(params.getOrDefault("phone", "13800138000")));
        user.setStatus(1);
        userService.save(user);
        return user;
    }

    @SampleDemo(
            id = "mybatis-get-user-by-id",
            name = "按 ID 查询用户",
            description = "演示 MyBatis-Plus 的 IService.getById() 方法，根据主键查询单条记录",
            category = "CRUD",
            module = "spring-boot-mybatis-plus",
            params = {
                    @SampleParam(name = "id", type = "Long", required = true,
                            description = "用户ID", example = "1")
            },
            configHints = {"需要 MySQL 数据库: localhost:3306/javastack"},
            tags = {"mybatis-plus", "crud", "query"}
    )
    public Object getUserById(Map<String, Object> params) {
        Long id = Long.valueOf(String.valueOf(params.getOrDefault("id", "1")));
        UserDO user = userService.getById(id);
        if (user == null) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "用户不存在");
            result.put("id", id);
            return result;
        }
        return user;
    }

    @SampleDemo(
            id = "mybatis-get-user-by-username",
            name = "按用户名查询用户",
            description = "演示两种查询方式：type=0 使用 XML Mapper SQL 查询，type=1 使用 LambdaQueryWrapper 条件构造器查询",
            category = "CRUD",
            module = "spring-boot-mybatis-plus",
            params = {
                    @SampleParam(name = "username", type = "String", required = true,
                            description = "用户名", example = "testuser"),
                    @SampleParam(name = "type", type = "Integer", required = true,
                            description = "查询方式：0=XML Mapper SQL, 1=LambdaQueryWrapper", example = "1")
            },
            configHints = {
                    "需要 MySQL 数据库: localhost:3306/javastack",
                    "type=0: 使用 UserMapper.xml 中定义的 selectByUsername SQL",
                    "type=1: 使用 LambdaQueryWrapper 动态构建查询条件"
            },
            tags = {"mybatis-plus", "crud", "query", "LambdaQueryWrapper"}
    )
    public Object getUserByUsername(Map<String, Object> params) {
        String username = String.valueOf(params.getOrDefault("username", "testuser"));
        int type = Integer.parseInt(String.valueOf(params.getOrDefault("type", "1")));
        UserDO user = userService.getByUsername(username, type);
        if (user == null) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "用户不存在");
            result.put("username", username);
            result.put("queryType", type == 0 ? "XML Mapper" : "LambdaQueryWrapper");
            return result;
        }
        return user;
    }

    @SampleDemo(
            id = "mybatis-auto-fill",
            name = "自动填充 createTime/updateTime",
            description = "演示 MyBatis-Plus MetaObjectHandler 自动填充机制，插入时自动填充 createTime，更新时自动填充 updateTime",
            category = "Meta",
            module = "spring-boot-mybatis-plus",
            configHints = {
                    "需要 MySQL 数据库: localhost:3306/javastack",
                    "CustomMetaObjectHandler 实现 MetaObjectHandler 接口",
                    "@TableField(fill = FieldFill.INSERT) 标注插入时自动填充的字段",
                    "@TableField(fill = FieldFill.INSERT_UPDATE) 标注插入和更新时自动填充的字段"
            },
            tags = {"mybatis-plus", "MetaObjectHandler", "auto-fill"}
    )
    public Object autoFillDemo(Map<String, Object> params) {
        UserDO user = new UserDO();
        user.setUsername("autofill_demo_" + System.currentTimeMillis());
        user.setPhone("13900000000");
        user.setStatus(1);
        // createTime 未手动设置，将由 MetaObjectHandler 自动填充
        userService.save(user);

        // 重新查询以验证 createTime 已自动填充
        UserDO saved = userService.getById(user.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "插入时未手动设置 createTime，由 MetaObjectHandler 自动填充");
        result.put("savedUser", saved);
        result.put("createTimeAutoFilled", saved != null && saved.getCreateTime() != null);
        return result;
    }

}
