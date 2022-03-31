package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@Table(value = "FACILITY_ALTERNATE_CODE_LINKS")
public class FacilityAlternateCodeLink implements Persistable<UUID> {

    @Id
    private UUID id;
    private String facilityId;
    private String alternateCodeId;

    @PersistenceConstructor
    public FacilityAlternateCodeLink() {
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