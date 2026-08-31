package com.cryptalk.media;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {
    private final MediaService media;

    public MediaController(MediaService media) { this.media = media; }

    @PostMapping
    MediaService.StoredMedia upload(@RequestParam("file") MultipartFile file) {
        return media.store(file);
    }

    @GetMapping("/{fileName}")
    ResponseEntity<Resource> get(@PathVariable String fileName) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(media.contentType(fileName)))
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
            .body(media.load(fileName));
    }
}
