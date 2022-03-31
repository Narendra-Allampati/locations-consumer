package com.maersk.referencedata.locationsconsumer.domains.locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 15/11/2021.
 */

@Builder
@AllArgsConstructor
@Table(value = "ALTERNATE_CODES")
public class AlternateCode implements Persistable<String> {

    @Id
    private String code;
    private String codeType;

    @PersistenceConstructor
    public AlternateCode() {
    }

    @Transient
    private boolean isNew;

    @Override
    public String getId() {
        return this.code;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNew || this.code == null;
    }
}
