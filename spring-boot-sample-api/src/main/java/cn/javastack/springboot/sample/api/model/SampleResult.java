package cn.javastack.springboot.sample.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 示例执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleResult {

    private String sampleId;
    private boolean success;
    private Object data;
    private String errorMessage;
    private long executionTimeMs;
    private Map<String, Object> requestParams;
    private List<String> configHints;

}
