package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@Builder
@Table(value = "CONTACT_DETAILS")
public class ContactDetail {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;
    private String internationalDialingCdPhone;
    private String extension;
    private String phoneNumber;
    private String internationalDialingCdMobile;
    private String mobileNumber;
    private String internationalDialingCdFax;
    private String faxNumber;
    private String emailAddress;
    private LocalDate validThroughDate;
}
