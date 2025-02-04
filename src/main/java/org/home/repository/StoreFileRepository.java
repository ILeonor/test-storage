package org.home.repository;

import org.home.repository.model.StoreFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoreFileRepository extends CrudRepository<StoreFile, Long> {
    List<StoreFile> findBySpaceId(Long spaceId);
}
