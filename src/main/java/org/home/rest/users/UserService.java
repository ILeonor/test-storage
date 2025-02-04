package org.home.rest.users;

import org.home.repository.StoreSpaceRepository;
import org.home.repository.UserRepository;
import org.home.repository.StoreSpaceAccessRepository;
import org.home.repository.model.StoreSpace;
import org.home.repository.model.StoreUser;
import org.home.repository.model.StoreSpaceAccess;
import org.home.rest.config.StoreUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
@RestController
@RequestMapping("/api/users")
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    StoreSpaceRepository storeSpaceRepository;

    @Autowired
    StoreSpaceAccessRepository spaceAccessRepository;

    @GetMapping("/me")
    public ResponseEntity<StoreUser> getMe(@AuthenticationPrincipal StoreUserDetails userDetails) {
        if (userDetails != null) {
            logger.debug(String.format("User -> {id: %d; name: %s}",
                    userDetails.storeUser.getId(), userDetails.storeUser.getFullName()));

            List<StoreSpaceAccess> spaces = spaceAccessRepository.findByUserId(userDetails.storeUser.getId());

            for (StoreSpaceAccess spaceAccess : spaces) {
                logger.debug(String.format("\tUserSpace -> {key: [%d:%d], id: %d, access right: %d}",
                        spaceAccess.getId().getUserId(), spaceAccess.getId().getSpaceId(),
                        spaceAccess.getSpace().getId(), spaceAccess.getAccessRight()));
            }

            return new ResponseEntity<>(userDetails.storeUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new")
    @Transactional
    public ResponseEntity<StoreUser> createUser(@RequestBody StoreUser user) {
        user.setIsAdmin(false);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        userRepository.save(user);

        StoreSpace space = new StoreSpace(user);

        storeSpaceRepository.save(space);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreUser> getUser(@PathVariable Long id) {
        Optional<StoreUser> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.debug("User -> {id: {}; name: {}}", user.get().getId(), user.get().getFullName());

            List<StoreSpaceAccess> spaces = spaceAccessRepository.findByUserId(user.get().getId());

            for (StoreSpaceAccess spaceAccess : spaces) {
                logger.debug("\tUserSpace -> {key: [{}:{}], id: {}, access right: {}}",
                        spaceAccess.getId().getUserId(), spaceAccess.getId().getSpaceId(),
                        spaceAccess.getSpace().getId(), spaceAccess.getAccessRight());
            }

            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StoreUser> deleteUser(@PathVariable Long id) {
        Optional<StoreUser> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.delete(user.get());

            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
