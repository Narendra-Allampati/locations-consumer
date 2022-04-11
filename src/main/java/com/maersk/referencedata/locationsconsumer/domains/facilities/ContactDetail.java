package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@AllArgsConstructor
@Builder
@Table(value = "CONTACT_DETAILS")
public class ContactDetail implements Persistable<String> {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;
    private String internationalDialingCodePhone;
    private String extension;
    private String phoneNumber;
    private String internationalDialingCodeMobile;
    private String mobileNumber;
    private String internationalDialingCodeFax;
    private String faxNumber;
    private String emailAddress;
    private LocalDate validThroughDate;

    @PersistenceConstructor
    public ContactDetail() {
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
