package com.cryptalk.media;

import com.cryptalk.common.ApiException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {
    private static final long MAX_BYTES = 25L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/webp", "webp",
        "image/gif", "gif",
        "video/mp4", "mp4",
        "video/webm", "webm",
        "video/quicktime", "mov"
    );

    private final Path root;

    public MediaService(@Value("${cryptalk.media.storage-path}") String storagePath) {
        this.root = Path.of(storagePath).toAbsolutePath().normalize();
    }

    @PostConstruct
    void initialize() {
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException("미디어 저장소를 초기화할 수 없습니다.", exception);
        }
    }

    public StoredMedia store(MultipartFile file) {
        if (file.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다.");
        if (file.getSize() > MAX_BYTES) throw new ApiException(HttpStatus.PAYLOAD_TOO_LARGE, "파일은 25MB 이하여야 합니다.");
        String contentType = file.getContentType();
        String extension = EXTENSIONS.get(contentType);
        if (extension == null) throw new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 이미지 또는 영상 형식입니다.");

        String fileName = UUID.randomUUID() + "." + extension;
        Path target = root.resolve(fileName).normalize();
        if (!target.getParent().equals(root)) throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 파일 이름입니다.");
        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장하지 못했습니다.");
        }
        String mediaType = contentType.startsWith("image/") ? "IMAGE" : "VIDEO";
        return new StoredMedia(mediaType, "/api/v1/media/" + fileName, contentType, file.getSize());
    }

    public Resource load(String fileName) {
        if (!fileName.matches("^[0-9a-f-]{36}\\.(jpg|png|webp|gif|mp4|webm|mov)$"))
            throw new ApiException(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다.");
        Path target = root.resolve(fileName).normalize();
        if (!target.getParent().equals(root) || !Files.isRegularFile(target))
            throw new ApiException(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다.");
        try {
            return new UrlResource(target.toUri());
        } catch (MalformedURLException exception) {
            throw new ApiException(HttpStatus.NOT_FOUND, "미디어를 찾을 수 없습니다.");
        }
    }

    public String contentType(String fileName) {
        if (fileName.endsWith(".jpg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".webp")) return "image/webp";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".mp4")) return "video/mp4";
        if (fileName.endsWith(".webm")) return "video/webm";
        if (fileName.endsWith(".mov")) return "video/quicktime";
        return "application/octet-stream";
    }

    public record StoredMedia(String mediaType, String url, String contentType, long size) {}
}
