package org.home.service;

import org.home.repository.StoreFileAccessRepository;
import org.home.repository.StoreSpaceAccessRepository;
import org.home.repository.StoreSpaceRepository;
import org.home.repository.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StoreUtils {
    private static StoreSpaceRepository       spaceRepository;
    private static StoreSpaceAccessRepository spaceAccessRepository;
    private static StoreFileAccessRepository  fileAccessRepository;

    public StoreUtils(@Autowired StoreSpaceRepository spaceRepository,
                      @Autowired StoreSpaceAccessRepository spaceAccessRepository,
                      @Autowired StoreFileAccessRepository fileAccessRepository) {
        StoreUtils.spaceRepository = spaceRepository;
        StoreUtils.spaceAccessRepository = spaceAccessRepository;
        StoreUtils.fileAccessRepository = fileAccessRepository;
    }

    public static StoreSpace getSpace(Long userId, Long spaceId, long accessRight) {
        StoreSpace selfSpace = spaceRepository.findByOwnerId(userId);

        if (spaceId == null || spaceId.equals(selfSpace.getId())) {
            return selfSpace;
        } else {
            StoreSpaceAccess spaceAccess = spaceAccessRepository.findByUserIdAndSpaceId(userId, spaceId);
            if (spaceAccess != null) {
                if ((spaceAccess.getAccessRight() & accessRight) != 0) {
                    return spaceAccess.getSpace();
                }
            }
        }

        return null;
    }

    public static long getFileAccess(StoreFile file, StoreUser user) {
        Long fileSpaceId = file.getSpace().getId();
        long result = StoreAccessRight.NONE;

        StoreSpace selfSpace = spaceRepository.findByOwner(user);
        if (!selfSpace.getId().equals(fileSpaceId)) {
            StoreSpaceAccess spaceAccess = spaceAccessRepository.findByUserIdAndSpaceId(user.getId(), fileSpaceId);
            if (spaceAccess != null) {
                result = spaceAccess.getAccessRight();
            }
        } else {
            result = StoreAccessRight.FULL;
        }

        if (result != StoreAccessRight.FULL) {
            StoreFileAccess fileAccess = fileAccessRepository.findByUserIdAndFileId(user.getId(), file.getId());
            if (fileAccess != null) {
                result |= fileAccess.getAccessRight();
            }
        }

        return result;
    }
}
