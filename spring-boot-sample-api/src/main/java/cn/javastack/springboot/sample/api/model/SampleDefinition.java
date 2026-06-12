package cn.javastack.springboot.sample.api.model;

import lombok.Data;

import java.util.List;

/**
 * 示例定义模型，描述一个可执行的示例元数据。
 * 微信公众号：Java技术栈
 */
@Data
public class SampleDefinition {

    private String id;
    private String name;
    private String description;
    private String category;
    private String module;
    private List<ParamInfo> params;
    private List<String> configHints;
    private List<String> tags;

    /**
     * 参数信息
     */
    @Data
    public static class ParamInfo {
        private String name;
        private String type;
        private boolean required;
        private String description;
        private String defaultValue;
        private String example;
    }
}
