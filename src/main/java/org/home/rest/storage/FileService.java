package org.home.rest.storage;

import org.home.repository.StoreFileAccessRepository;
import org.home.repository.StoreFileRepository;
import org.home.repository.StoreSpaceRepository;
import org.home.repository.StoreSpaceAccessRepository;
import org.home.repository.model.*;
import org.home.rest.config.StoreUserDetails;
import org.home.rest.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RestController
@RequestMapping("/api/storage")
public class FileService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    StoreFileRepository storeFileRepository;

    @Autowired
    StoreSpaceRepository storeSpaceRepository;

    @Autowired
    StoreSpaceAccessRepository spaceAccessRepository;

    @Autowired
    StoreFileAccessRepository fileAccessRepository;

    private StoreSpace getSpace(StoreUser user, Long spaceId, long accessRight) {
        StoreSpace selfSpace = storeSpaceRepository.findByOwner(user);

        if (spaceId == null || spaceId.equals(selfSpace.getId())) {
            return selfSpace;
        } else {
            StoreSpaceAccess spaceAccess = spaceAccessRepository.findByUserIdAndSpaceId(user.getId(), spaceId);
            if (spaceAccess != null) {
                if ((spaceAccess.getAccessRight() & accessRight) != 0) {
                    return spaceAccess.getSpace();
                }
            }
        }

        return null;
    }

    @PostMapping("/files/new")
    public ResponseEntity<Object> uploadFile(@AuthenticationPrincipal StoreUserDetails userDetails,
                                             @RequestParam("file") MultipartFile httpFile,
                                             @RequestParam(value = "spaceId", required = false) Long spaceId) {
        if (httpFile.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }

        logger.debug("Space[{}], Upload file: '{}', size: {}",
                spaceId, httpFile.getOriginalFilename(), httpFile.getSize());

        StoreSpace space = getSpace(userDetails.storeUser, spaceId, StoreAccessRight.WRITE);
        if (space == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        /* TODO: Check free space */

        try {
            StoreFile file = new StoreFile(space);

            file.setName(httpFile.getOriginalFilename());
            file.setType(httpFile.getContentType());
            file.setData(httpFile.getBytes());

            storeFileRepository.save(file);

            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (IOException exception) {
            return new ResponseEntity<>("failed read file!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/files/list")
    public ResponseEntity<List<StoreFile>> listFiles(@AuthenticationPrincipal StoreUserDetails userDetails,
                                                     @RequestParam(value = "spaceId", required = false) Long spaceId) {
        StoreSpace space = getSpace(userDetails.storeUser, spaceId, StoreAccessRight.READ);
        if (space == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(storeFileRepository.findBySpaceId(space.getId()), HttpStatus.OK);
    }

    private long getFileAccess(StoreFile file, StoreUser user) {
        Long fileSpaceId = file.getSpace().getId();
        long result = StoreAccessRight.NONE;

        StoreSpace selfSpace = storeSpaceRepository.findByOwner(user);
        if (!selfSpace.getId().equals(fileSpaceId)) {
            StoreSpaceAccess spaceAccess = spaceAccessRepository.findByUserIdAndSpaceId(user.getId(), fileSpaceId);
            if (spaceAccess != null) {
                result = spaceAccess.getAccessRight();
            }
        } else {
            result = StoreAccessRight.CONTROL;
        }

        if (result != StoreAccessRight.CONTROL) {
            StoreFileAccess fileAccess = fileAccessRepository.findByUserIdAndFileId(user.getId(), file.getId());
            if (fileAccess != null) {
                result |= fileAccess.getAccessRight();
            }
        }

        return result;
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@AuthenticationPrincipal StoreUserDetails userDetails,
                                               @PathVariable Long fileId) {
        Optional<StoreFile> opFile = storeFileRepository.findById(fileId);
        if (opFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StoreFile file = opFile.get();

        if ((getFileAccess(file, userDetails.storeUser) & StoreAccessRight.READ) != 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(file.getData());
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<StoreFile> deleteFile(@AuthenticationPrincipal StoreUserDetails userDetails,
                                                @PathVariable Long fileId) {
        Optional<StoreFile> opFile = storeFileRepository.findById(fileId);
        if (opFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StoreFile file = opFile.get();
        if ((getFileAccess(file, userDetails.storeUser) & StoreAccessRight.WRITE) != 0) {
            storeFileRepository.delete(file);

            return new ResponseEntity<>(file, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
