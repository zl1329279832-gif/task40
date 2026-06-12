package cn.javastack.springboot.sample.api.controller;

import cn.javastack.springboot.sample.api.model.SampleDefinition;
import cn.javastack.springboot.sample.api.model.SampleResult;
import cn.javastack.springboot.sample.api.registry.SampleRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 示例目录查询与在线体验 API
 */
@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {

    private final SampleRegistry sampleRegistry;

    @GetMapping
    public List<SampleDefinition> listSamples() {
        return sampleRegistry.getAllDefinitions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleDefinition> getSample(@PathVariable String id) {
        SampleDefinition definition = sampleRegistry.getDefinition(id);
        if (definition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(definition);
    }

    @PostMapping("/{id}/execute")
    public SampleResult executeSample(@PathVariable String id,
                                      @RequestBody(required = false) Map<String, Object> params) {
        return sampleRegistry.execute(id, params != null ? params : Map.of());
    }

}
