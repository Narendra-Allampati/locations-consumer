package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@Builder
@Table(value = "OPENING_HOURS")
public class OpeningHour implements Persistable<Long> {

    @Id
    private String id;
    private String day;
    private String openTimeHours;
    private String openTimeMinutes;
    private String closeTimeHours;
    private String closeTimeMinutes;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
