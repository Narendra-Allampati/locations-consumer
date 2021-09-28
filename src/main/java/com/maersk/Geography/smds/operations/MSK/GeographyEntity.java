/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.maersk.Geography.smds.operations.MSK;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class GeographyEntity extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2138969968519002863L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GeographyEntity\",\"namespace\":\"com.maersk.Geography.smds.operations.MSK\",\"fields\":[{\"name\":\"Geography\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Geography\",\"doc\":\"Geography Entity Information Which includes AltNm,AltCd,fence,Country,Parent,BDA and BDALoc \",\"fields\":[{\"name\":\"GeoRowID\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeoType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Status\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"ValidFrom\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"ValidTo\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Longitude\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Latitude\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"TimeZone\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"DaylightSavingTime\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"UTCOffsetMinutes\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"DaylightSavingStart\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"DaylightSavingEnd\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"DaylightSavingShiftMinutes\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Description\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"WorkaroundReason\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Restricted\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"SiteType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GPSFlag\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GSMFlag\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"StreetNumber\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"AddressLine1\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"AddressLine2\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"AddressLine3\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"PostalCode\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"PostalCodeMandatoryFlag\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"StateProvienceMandatory\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"DialingCode\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"DialingCodedescription\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"PortFlag\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"OlsonTimezone\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"BDAType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographyAlternateNames\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyAlternateNames\",\"doc\":\"Geography AlternateNames details\",\"fields\":[{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Description\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Status\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]},{\"name\":\"GeographyAlternateCodes\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyAlternateCodes\",\"doc\":\"Geography AlternateCodes details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}},{\"name\":\"GeographyFence\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyFence\",\"doc\":\"Geography Fence details\",\"fields\":[{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeoFenceType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]},{\"name\":\"GeographyCountryDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyCountryDetails\",\"doc\":\"Geography Country details\",\"fields\":[{\"name\":\"countryRowid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographyCountryAlternateCodeDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyCountryAlternateCode\",\"doc\":\"Geography Country AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]}]}}]},{\"name\":\"GeographyParentDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyParentDetails\",\"doc\":\"Geography Parent details\",\"fields\":[{\"name\":\"getParentRowId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographyParentAlternateCodeDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyParentAlternateCode\",\"doc\":\"Geography Parent AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]}]}}]},{\"name\":\"GeographySubCityParentDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographySubCityParentDetails\",\"doc\":\"Geography SubCityParent details\",\"fields\":[{\"name\":\"getSubCityParentRowId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographySubCityParentAlternateCodeDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographySubCityParentAlternateCode\",\"doc\":\"Geography SubCityParent AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]}]}}]},{\"name\":\"GeographyBDADetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyBDADetails\",\"doc\":\"Geography BDA details\",\"fields\":[{\"name\":\"bdaRowid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"BDAType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographyBDAAlternateCodeDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyBDAAlternateCode\",\"doc\":\"Geography BDA AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]}]}}]},{\"name\":\"GeographyBDALocationsDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyBDALocationDetails\",\"doc\":\"Geography BDA Location details\",\"fields\":[{\"name\":\"bdaLocRowid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographyBDALocationAlternateCodeDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyBDALocationAlternateCode\",\"doc\":\"Geography BDALocation AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]}]}}]}]}]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<GeographyEntity> ENCODER =
      new BinaryMessageEncoder<GeographyEntity>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<GeographyEntity> DECODER =
      new BinaryMessageDecoder<GeographyEntity>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<GeographyEntity> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<GeographyEntity> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<GeographyEntity> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<GeographyEntity>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this GeographyEntity to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a GeographyEntity from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a GeographyEntity instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static GeographyEntity fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private com.maersk.Geography.smds.operations.MSK.Geography Geography;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public GeographyEntity() {}

  /**
   * All-args constructor.
   * @param Geography The new value for Geography
   */
  public GeographyEntity(com.maersk.Geography.smds.operations.MSK.Geography Geography) {
    this.Geography = Geography;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return Geography;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: Geography = (com.maersk.Geography.smds.operations.MSK.Geography)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'Geography' field.
   * @return The value of the 'Geography' field.
   */
  public com.maersk.Geography.smds.operations.MSK.Geography getGeography() {
    return Geography;
  }


  /**
   * Sets the value of the 'Geography' field.
   * @param value the value to set.
   */
  public void setGeography(com.maersk.Geography.smds.operations.MSK.Geography value) {
    this.Geography = value;
  }

  /**
   * Creates a new GeographyEntity RecordBuilder.
   * @return A new GeographyEntity RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder newBuilder() {
    return new com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder();
  }

  /**
   * Creates a new GeographyEntity RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new GeographyEntity RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder(other);
    }
  }

  /**
   * Creates a new GeographyEntity RecordBuilder by copying an existing GeographyEntity instance.
   * @param other The existing instance to copy.
   * @return A new GeographyEntity RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyEntity other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder(other);
    }
  }

  /**
   * RecordBuilder for GeographyEntity instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GeographyEntity>
    implements org.apache.avro.data.RecordBuilder<GeographyEntity> {

    private com.maersk.Geography.smds.operations.MSK.Geography Geography;
    private com.maersk.Geography.smds.operations.MSK.Geography.Builder GeographyBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.Geography)) {
        this.Geography = data().deepCopy(fields()[0].schema(), other.Geography);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (other.hasGeographyBuilder()) {
        this.GeographyBuilder = com.maersk.Geography.smds.operations.MSK.Geography.newBuilder(other.getGeographyBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing GeographyEntity instance
     * @param other The existing instance to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyEntity other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.Geography)) {
        this.Geography = data().deepCopy(fields()[0].schema(), other.Geography);
        fieldSetFlags()[0] = true;
      }
      this.GeographyBuilder = null;
    }

    /**
      * Gets the value of the 'Geography' field.
      * @return The value.
      */
    public com.maersk.Geography.smds.operations.MSK.Geography getGeography() {
      return Geography;
    }


    /**
      * Sets the value of the 'Geography' field.
      * @param value The value of 'Geography'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder setGeography(com.maersk.Geography.smds.operations.MSK.Geography value) {
      validate(fields()[0], value);
      this.GeographyBuilder = null;
      this.Geography = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'Geography' field has been set.
      * @return True if the 'Geography' field has been set, false otherwise.
      */
    public boolean hasGeography() {
      return fieldSetFlags()[0];
    }

    /**
     * Gets the Builder instance for the 'Geography' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public com.maersk.Geography.smds.operations.MSK.Geography.Builder getGeographyBuilder() {
      if (GeographyBuilder == null) {
        if (hasGeography()) {
          setGeographyBuilder(com.maersk.Geography.smds.operations.MSK.Geography.newBuilder(Geography));
        } else {
          setGeographyBuilder(com.maersk.Geography.smds.operations.MSK.Geography.newBuilder());
        }
      }
      return GeographyBuilder;
    }

    /**
     * Sets the Builder instance for the 'Geography' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder setGeographyBuilder(com.maersk.Geography.smds.operations.MSK.Geography.Builder value) {
      clearGeography();
      GeographyBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'Geography' field has an active Builder instance
     * @return True if the 'Geography' field has an active Builder instance
     */
    public boolean hasGeographyBuilder() {
      return GeographyBuilder != null;
    }

    /**
      * Clears the value of the 'Geography' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyEntity.Builder clearGeography() {
      Geography = null;
      GeographyBuilder = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GeographyEntity build() {
      try {
        GeographyEntity record = new GeographyEntity();
        if (GeographyBuilder != null) {
          try {
            record.Geography = this.GeographyBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("Geography"));
            throw e;
          }
        } else {
          record.Geography = fieldSetFlags()[0] ? this.Geography : (com.maersk.Geography.smds.operations.MSK.Geography) defaultValue(fields()[0]);
        }
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<GeographyEntity>
    WRITER$ = (org.apache.avro.io.DatumWriter<GeographyEntity>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<GeographyEntity>
    READER$ = (org.apache.avro.io.DatumReader<GeographyEntity>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    if (this.Geography == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      this.Geography.customEncode(out);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      if (in.readIndex() != 1) {
        in.readNull();
        this.Geography = null;
      } else {
        if (this.Geography == null) {
          this.Geography = new com.maersk.Geography.smds.operations.MSK.Geography();
        }
        this.Geography.customDecode(in);
      }

    } else {
      for (int i = 0; i < 1; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Geography = null;
          } else {
            if (this.Geography == null) {
              this.Geography = new com.maersk.Geography.smds.operations.MSK.Geography();
            }
            this.Geography.customDecode(in);
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









