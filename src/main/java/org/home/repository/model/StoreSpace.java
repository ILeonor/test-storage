package org.home.repository.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "STORE_SPACES")
@Data
public class StoreSpace {
    @Id
    @SequenceGenerator(name="storeSeq", sequenceName = "store_sq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "storeSeq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public StoreUser owner;

    protected StoreSpace() {};

    public StoreSpace(StoreUser owner) {
        this.owner = owner;
    }
}
