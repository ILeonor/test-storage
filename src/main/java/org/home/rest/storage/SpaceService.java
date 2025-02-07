package org.home.rest.storage;

import jakarta.transaction.Transactional;
import org.home.repository.StoreSpaceAccessRepository;
import org.home.repository.model.StoreAccessRight;
import org.home.repository.model.StoreSpace;
import org.home.repository.model.StoreSpaceAccess;
import org.home.repository.model.StoreUser;
import org.home.rest.config.StoreUserDetails;
import org.home.service.StoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Service
@RestController
@RequestMapping("/api/storage/spaces")
public class SpaceService {
    @Autowired
    StoreSpaceAccessRepository spaceAccessRepository;

    @PostMapping("{spaceId}/grant")
    @Transactional
    public ResponseEntity<String> grant(@AuthenticationPrincipal StoreUserDetails userDetails,
                                        @PathVariable Long spaceId, @RequestBody Map<String, Object> data) {
        Long userId         = ((Number) data.get("userId")).longValue();
        Long newAccessRight = ((Number) data.get("accessRight")).longValue() & StoreAccessRight.FULL;

        if (userId.equals(userDetails.storeUser.getId())) {
            return ResponseEntity.badRequest().build();
        }

        StoreSpace space = StoreUtils.getSpace(userDetails.storeUser.getId(), spaceId, StoreAccessRight.CONTROL);
        if (space == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (space.getOwner().getId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        StoreSpaceAccess spaceAccess = spaceAccessRepository.findByUserIdAndSpaceId(userId, spaceId);
        if (spaceAccess != null) {
            spaceAccess.setAccessRight(newAccessRight);
        } else {
            StoreUser user = new StoreUser();

            user.setId(userId);

            spaceAccess = new StoreSpaceAccess(user, space);
            spaceAccess.setAccessRight(newAccessRight);

            spaceAccessRepository.save(spaceAccess);
        }

        return ResponseEntity.ok("success");
    }
}
