package cn.javastack.springboot.sample.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 示例执行结果模型。
 * 微信公众号：Java技术栈
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleResult {

    /** 示例 ID */
    private String sampleId;

    /** 是否执行成功 */
    private boolean success;

    /** 执行返回数据 */
    private Object data;

    /** 异常信息（失败时） */
    private String errorMessage;

    /** 执行耗时（毫秒） */
    private long executionTimeMs;

    /** 请求参数 */
    private Map<String, Object> requestParams;

    /** 相关配置提示 */
    private List<String> configHints;
}
