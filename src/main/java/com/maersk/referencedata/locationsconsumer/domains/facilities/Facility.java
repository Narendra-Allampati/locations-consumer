package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Anders Clausen on 18/11/2021.
 */
@AllArgsConstructor
@Builder
@Table(value = "FACILITIES")
public class Facility implements Persistable<String> {

    @Id
    private String id;
    private String name;
    private String type;
    private boolean extOwned;
    private String status;
    private boolean extExposed;
    private String url;
    private String departmentOfDefenceActivityAddressCode;
    private String parentName;
    private String parentType;

    @PersistenceConstructor
    public Facility() {
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
