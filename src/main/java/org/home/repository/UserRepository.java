package org.home.repository;

import org.home.repository.model.StoreUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends CrudRepository<StoreUser, Long> {
    StoreUser findByLogin(String login);
}
