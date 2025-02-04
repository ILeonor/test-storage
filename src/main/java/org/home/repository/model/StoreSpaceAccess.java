package org.home.repository.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "STORE_SPACE_ACCESS")
@Data
public class StoreSpaceAccess {
    @EmbeddedId
    UserSpaceKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    public StoreUser user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("spaceId")
    @JoinColumn(name = "space_id")
    public StoreSpace space;

    @Column(name = "access_right")
    public Long accessRight;

    protected StoreSpaceAccess() {
    }

    public StoreSpaceAccess(StoreUser user, StoreSpace space) {
        this.id = new UserSpaceKey(user.getId(), space.getId());
        this.user = user;
        this.space = space;
    }

    @Embeddable
    @Data
    public static class UserSpaceKey implements Serializable {

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "space_id")
        private Long spaceId;

        protected UserSpaceKey() {
        }

        public UserSpaceKey(Long userId, Long spaceId) {
            this.userId = userId;
            this.spaceId = spaceId;
        }
    }
}