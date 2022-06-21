package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@AllArgsConstructor
@Builder
@Table(value = "FENCES")
public class Fence implements Persistable<String> {

    @Id
    private String id;
    private String facilityId;
    private String name;
    private String fenceType;

    @PersistenceCreator
    public Fence() {
    }

    @Transient
    private boolean isNew;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNew || this.id == null;
    }
}
