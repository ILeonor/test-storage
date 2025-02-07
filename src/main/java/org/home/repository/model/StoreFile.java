package org.home.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "STORE_FILES")
@Data
public class StoreFile {
    @Id
    @SequenceGenerator(name="storeSeq", sequenceName = "store_sq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "storeSeq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "space_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private StoreSpace space;

    private String name;

    private String type;

    @Column(name = "data_size")
    private Long dataSize;

    @Column(name = "data", columnDefinition = "bytea")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] data;

    protected StoreFile(){};

    public StoreFile(StoreSpace space) {
        this.space = space;
    }
}
