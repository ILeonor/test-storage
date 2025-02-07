package org.home.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "STORE_USER")
@Data
public class StoreUser {
    @Id
    @SequenceGenerator(name="storeSeq", sequenceName = "store_sq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "storeSeq")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String login;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    public StoreUser() {}

    public StoreUser(String name) {
        this.fullName = name;
    }

    @Override
    public String toString() {
        return String.format("StoreUser[id=%d, name='%s']", id, fullName);
    }
}
