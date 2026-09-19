package com.cryptalk.media;

import com.cryptalk.common.ApiException;
import com.cryptalk.common.ErrorCode;
import com.cryptalk.member.Member;
import com.cryptalk.member.MemberRepository;
import com.cryptalk.post.Post;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    private final MediaAssetRepository assets;
    private final MemberRepository members;

    public MediaService(@Value("${cryptalk.media.storage-path}") String storagePath,
                        MediaAssetRepository assets, MemberRepository members) {
        this.root = Path.of(storagePath).toAbsolutePath().normalize();
        this.assets = assets; this.members = members;
    }

    @PostConstruct
    void initialize() {
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException("미디어 저장소를 초기화할 수 없습니다.", exception);
        }
    }

    @Transactional
    public StoredMedia store(Long memberId, MultipartFile file) {
        Member member = members.findById(memberId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (file.isEmpty()) throw new ApiException(ErrorCode.EMPTY_MEDIA_FILE);
        if (file.getSize() > MAX_BYTES) throw new ApiException(ErrorCode.MEDIA_TOO_LARGE);
        String contentType = file.getContentType();
        String extension = EXTENSIONS.get(contentType);
        if (extension == null) throw new ApiException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);

        String fileName = UUID.randomUUID() + "." + extension;
        Path target = root.resolve(fileName).normalize();
        if (!target.getParent().equals(root)) throw new ApiException(ErrorCode.INVALID_FILE_NAME);
        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException exception) {
            throw new ApiException(ErrorCode.MEDIA_STORAGE_ERROR);
        }
        String mediaType = contentType.startsWith("image/") ? "IMAGE" : "VIDEO";
        assets.save(new MediaAsset(fileName, member, mediaType, contentType, file.getSize()));
        deleteOnRollback(fileName);
        return new StoredMedia(mediaType, "/api/v1/media/" + fileName, contentType, file.getSize());
    }

    @Transactional
    public void delete(Long memberId, String fileName) {
        MediaAsset asset = assets.findById(fileName)
            .orElseThrow(() -> new ApiException(ErrorCode.MEDIA_NOT_FOUND));
        if (!asset.getMember().getId().equals(memberId))
            throw new ApiException(ErrorCode.MEDIA_FORBIDDEN, "업로드한 사용자만 미디어를 삭제할 수 있습니다.");
        if (asset.getPost() != null)
            throw new ApiException(ErrorCode.MEDIA_IN_USE, "게시글에 연결된 미디어는 게시글 수정 또는 삭제로 제거해 주세요.");
        assets.delete(asset);
        deleteAfterCommit(Set.of(fileName));
    }

    public void claim(Long memberId, Post post, String url, boolean allowLegacyMissing) {
        String fileName = managedFileName(url);
        if (fileName == null) return;
        MediaAsset asset = assets.findById(fileName).orElse(null);
        if (asset == null && allowLegacyMissing) return;
        if (asset == null) throw new ApiException(ErrorCode.MEDIA_NOT_UPLOADED);
        if (!asset.getMember().getId().equals(memberId))
            throw new ApiException(ErrorCode.MEDIA_FORBIDDEN, "본인이 업로드한 미디어만 게시할 수 있습니다.");
        if (asset.getPost() != null && !asset.getPost().getId().equals(post.getId()))
            throw new ApiException(ErrorCode.MEDIA_ALREADY_LINKED);
        asset.attach(post);
    }

    public void deleteManagedAfterCommit(Collection<String> urls) {
        Set<String> fileNames = new LinkedHashSet<>();
        for (String url : urls) {
            String fileName = managedFileName(url);
            if (fileName != null) fileNames.add(fileName);
        }
        if (fileNames.isEmpty()) return;
        assets.deleteByFileNameIn(fileNames);
        deleteAfterCommit(fileNames);
    }

    public Resource load(String fileName) {
        if (!validFileName(fileName))
            throw new ApiException(ErrorCode.MEDIA_NOT_FOUND);
        Path target = root.resolve(fileName).normalize();
        if (!target.getParent().equals(root) || !Files.isRegularFile(target))
            throw new ApiException(ErrorCode.MEDIA_NOT_FOUND);
        try {
            return new UrlResource(target.toUri());
        } catch (MalformedURLException exception) {
            throw new ApiException(ErrorCode.MEDIA_NOT_FOUND);
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

    private String managedFileName(String url) {
        if (url == null || !url.startsWith("/api/v1/media/")) return null;
        String fileName = url.substring("/api/v1/media/".length());
        return validFileName(fileName) ? fileName : null;
    }

    private boolean validFileName(String fileName) {
        return fileName.matches("^[0-9a-f-]{36}\\.(jpg|png|webp|gif|mp4|webm|mov)$");
    }

    private void deleteOnRollback(String fileName) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) deleteNow(fileName);
            }
        });
    }

    private void deleteAfterCommit(Set<String> fileNames) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            fileNames.forEach(this::deleteNow); return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { fileNames.forEach(MediaService.this::deleteNow); }
        });
    }

    private void deleteNow(String fileName) {
        if (!validFileName(fileName)) return;
        try { Files.deleteIfExists(root.resolve(fileName).normalize()); }
        catch (IOException ignored) { /* Database state remains authoritative; a cleanup job may retry orphan files. */ }
    }

    public record StoredMedia(String mediaType, String url, String contentType, long size) {}
}
