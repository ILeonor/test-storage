package org.home.repository;

import org.home.repository.model.StoreSpaceAccess;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface StoreSpaceAccessRepository extends CrudRepository<StoreSpaceAccess, StoreSpaceAccess.UserSpaceKey> {
    List<StoreSpaceAccess> findByUserId(Long userId);

    StoreSpaceAccess findByUserIdAndSpaceId(Long userId, Long spaceId);
}
