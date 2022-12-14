package com.maersk.referencedata.locationsconsumer.domains.locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@Table(value = "GEO_ALTERNATE_CODE_LINKS")
public class GeoAlternateCodeLink implements Persistable<UUID> {

    @Id
    private UUID id;
    private String geoId;
    private String alternateCodeId;
    private String alternateCodeType;

    @PersistenceCreator
    public GeoAlternateCodeLink() {
    }

    @Transient
    @Builder.Default
    private boolean isNew = true;

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
