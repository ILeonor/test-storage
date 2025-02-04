package org.home.rest.config;

import org.home.repository.UserRepository;
import org.home.repository.model.StoreUser;
import org.home.rest.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StoreUserDetailsService implements UserDetailsService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StoreUser user = userRepository.findByLogin(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new StoreUserDetails(user);
    }
}

