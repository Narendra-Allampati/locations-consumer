package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Anders Clausen on 18/11/2021.
 */
@AllArgsConstructor
@Builder
@Data
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
    private String parentId;
    private String parentName;
    private String parentType;
    private String rkst;
    private String rkts;
    private String unloc;
    private String unlocReturn;
    private String unlocLookup;
    private String scheduleD;
    private String scheduleK;
    private String lnsCode;
    private String lnsUnCode;
    private String hsudCode;
    private String hsudNumber;
    private String businessUnitId;
    private String customsLoc;
    private String smdg;
    private String bic;
    private String geoId;
    private String iata;

    @PersistenceCreator
    public Facility() {
    }

    @Transient
    @Builder.Default
    private boolean isNew = true;

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
