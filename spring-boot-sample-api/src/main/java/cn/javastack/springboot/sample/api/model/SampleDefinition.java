package cn.javastack.springboot.sample.api.model;

import lombok.Data;

import java.util.List;

/**
 * 示例元数据定义
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
