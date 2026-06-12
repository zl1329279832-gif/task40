package cn.javastack.springboot.sample.api.controller;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 示例目录与在线体验 API 控制器。
 * 微信公众号：Java技术栈
 */
@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private final SampleRegistry sampleRegistry;

    public SampleController(SampleRegistry sampleRegistry) {
        this.sampleRegistry = sampleRegistry;
    }

    /**
     * 查询所有示例目录
     */
    @GetMapping
    public List<SampleDefinition> listSamples() {
        return sampleRegistry.getAllDefinitions();
    }

    /**
     * 查询单个示例详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SampleDefinition> getSample(@PathVariable String id) {
        SampleDefinition definition = sampleRegistry.getDefinition(id);
        if (definition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(definition);
    }

    /**
     * 执行示例
     */
    @PostMapping("/{id}/execute")
    public SampleResult executeSample(@PathVariable String id,
                                      @RequestBody(required = false) Map<String, Object> params) {
        return sampleRegistry.execute(id, params);
    }
}
