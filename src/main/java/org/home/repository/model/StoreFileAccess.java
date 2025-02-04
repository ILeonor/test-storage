package org.home.repository.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "STORE_FILE_ACCESS")
@Data
public class StoreFileAccess {
    @EmbeddedId
    FileAccessKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    public StoreUser user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    public StoreFile file;

    @Column(name = "access_right")
    public Long accessRight;

    protected StoreFileAccess() {
    }

    public StoreFileAccess(StoreUser user, StoreFile file) {
        this.id = new FileAccessKey(user.getId(), file.getId());
        this.user = user;
        this.file = file;
    }

    @Embeddable
    @Data
    public static class FileAccessKey implements Serializable {

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "file_id")
        private Long fileId;

        protected FileAccessKey() {
        }

        public FileAccessKey(Long userId, Long fileId) {
            this.userId = userId;
            this.fileId = fileId;
        }
    }
}
