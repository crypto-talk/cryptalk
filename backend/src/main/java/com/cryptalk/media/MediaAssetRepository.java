package com.cryptalk.media;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, String> {
    @Modifying
    @Query("delete from MediaAsset asset where asset.fileName in :fileNames")
    int deleteByFileNameIn(@Param("fileNames") Collection<String> fileNames);
}
