package com.maersk.referencedata.locationsconsumer.domains.locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
@AllArgsConstructor
@Table(value = "ALTERNATE_NAMES")
public class AlternateName implements Persistable<UUID> {

    @Id
    private UUID id;
    private String geoId;
    private String name;
    private String description;
    private String status;

    @PersistenceCreator
    public AlternateName() {
    }

    @Transient
    private boolean isNew;

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNew || this.id == null;
    }
}
