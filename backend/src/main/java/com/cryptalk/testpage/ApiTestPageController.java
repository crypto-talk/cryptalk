package com.cryptalk.testpage;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@ConditionalOnProperty(prefix = "cryptalk.test-pages", name = "enabled", havingValue = "true")
public class ApiTestPageController {
    @GetMapping(value = "/test/api", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<Resource> apiTestPage() {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .cacheControl(CacheControl.noStore())
            .body(new ClassPathResource("test-pages/api.html"));
    }
}
