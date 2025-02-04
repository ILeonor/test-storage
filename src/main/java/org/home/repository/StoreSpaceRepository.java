package org.home.repository;

import org.home.repository.model.StoreSpace;
import org.home.repository.model.StoreUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface StoreSpaceRepository extends CrudRepository<StoreSpace, Long> {
    StoreSpace findByOwner(StoreUser user);
}
