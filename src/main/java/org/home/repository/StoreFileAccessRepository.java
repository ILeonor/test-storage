package org.home.repository;

import org.home.repository.model.StoreFileAccess;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoreFileAccessRepository  extends CrudRepository<StoreFileAccess, StoreFileAccess.FileAccessKey> {
    List<StoreFileAccess> findByUserId(Long userId);

    StoreFileAccess findByUserIdAndFileId(Long userId, Long fileId);
}