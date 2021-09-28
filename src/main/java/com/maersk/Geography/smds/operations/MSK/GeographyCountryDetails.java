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

/** Geography Country details */
@org.apache.avro.specific.AvroGenerated
public class GeographyCountryDetails extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 8327334392005670111L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GeographyCountryDetails\",\"namespace\":\"com.maersk.Geography.smds.operations.MSK\",\"doc\":\"Geography Country details\",\"fields\":[{\"name\":\"countryRowid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"GeographyCountryAlternateCodeDetails\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GeographyCountryAlternateCode\",\"doc\":\"Geography Country AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}}]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<GeographyCountryDetails> ENCODER =
      new BinaryMessageEncoder<GeographyCountryDetails>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<GeographyCountryDetails> DECODER =
      new BinaryMessageDecoder<GeographyCountryDetails>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<GeographyCountryDetails> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<GeographyCountryDetails> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<GeographyCountryDetails> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<GeographyCountryDetails>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this GeographyCountryDetails to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a GeographyCountryDetails from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a GeographyCountryDetails instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static GeographyCountryDetails fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String countryRowid;
   private java.lang.String Name;
   private java.lang.String Type;
   private java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> GeographyCountryAlternateCodeDetails;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public GeographyCountryDetails() {}

  /**
   * All-args constructor.
   * @param countryRowid The new value for countryRowid
   * @param Name The new value for Name
   * @param Type The new value for Type
   * @param GeographyCountryAlternateCodeDetails The new value for GeographyCountryAlternateCodeDetails
   */
  public GeographyCountryDetails(java.lang.String countryRowid, java.lang.String Name, java.lang.String Type, java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> GeographyCountryAlternateCodeDetails) {
    this.countryRowid = countryRowid;
    this.Name = Name;
    this.Type = Type;
    this.GeographyCountryAlternateCodeDetails = GeographyCountryAlternateCodeDetails;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return countryRowid;
    case 1: return Name;
    case 2: return Type;
    case 3: return GeographyCountryAlternateCodeDetails;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: countryRowid = value$ != null ? value$.toString() : null; break;
    case 1: Name = value$ != null ? value$.toString() : null; break;
    case 2: Type = value$ != null ? value$.toString() : null; break;
    case 3: GeographyCountryAlternateCodeDetails = (java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'countryRowid' field.
   * @return The value of the 'countryRowid' field.
   */
  public java.lang.String getCountryRowid() {
    return countryRowid;
  }


  /**
   * Sets the value of the 'countryRowid' field.
   * @param value the value to set.
   */
  public void setCountryRowid(java.lang.String value) {
    this.countryRowid = value;
  }

  /**
   * Gets the value of the 'Name' field.
   * @return The value of the 'Name' field.
   */
  public java.lang.String getName() {
    return Name;
  }


  /**
   * Sets the value of the 'Name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.Name = value;
  }

  /**
   * Gets the value of the 'Type' field.
   * @return The value of the 'Type' field.
   */
  public java.lang.String getType() {
    return Type;
  }


  /**
   * Sets the value of the 'Type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.String value) {
    this.Type = value;
  }

  /**
   * Gets the value of the 'GeographyCountryAlternateCodeDetails' field.
   * @return The value of the 'GeographyCountryAlternateCodeDetails' field.
   */
  public java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> getGeographyCountryAlternateCodeDetails() {
    return GeographyCountryAlternateCodeDetails;
  }


  /**
   * Sets the value of the 'GeographyCountryAlternateCodeDetails' field.
   * @param value the value to set.
   */
  public void setGeographyCountryAlternateCodeDetails(java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> value) {
    this.GeographyCountryAlternateCodeDetails = value;
  }

  /**
   * Creates a new GeographyCountryDetails RecordBuilder.
   * @return A new GeographyCountryDetails RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder newBuilder() {
    return new com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder();
  }

  /**
   * Creates a new GeographyCountryDetails RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new GeographyCountryDetails RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder(other);
    }
  }

  /**
   * Creates a new GeographyCountryDetails RecordBuilder by copying an existing GeographyCountryDetails instance.
   * @param other The existing instance to copy.
   * @return A new GeographyCountryDetails RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder(other);
    }
  }

  /**
   * RecordBuilder for GeographyCountryDetails instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GeographyCountryDetails>
    implements org.apache.avro.data.RecordBuilder<GeographyCountryDetails> {

    private java.lang.String countryRowid;
    private java.lang.String Name;
    private java.lang.String Type;
    private java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> GeographyCountryAlternateCodeDetails;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.countryRowid)) {
        this.countryRowid = data().deepCopy(fields()[0].schema(), other.countryRowid);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.Name)) {
        this.Name = data().deepCopy(fields()[1].schema(), other.Name);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.Type)) {
        this.Type = data().deepCopy(fields()[2].schema(), other.Type);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.GeographyCountryAlternateCodeDetails)) {
        this.GeographyCountryAlternateCodeDetails = data().deepCopy(fields()[3].schema(), other.GeographyCountryAlternateCodeDetails);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
    }

    /**
     * Creates a Builder by copying an existing GeographyCountryDetails instance
     * @param other The existing instance to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.countryRowid)) {
        this.countryRowid = data().deepCopy(fields()[0].schema(), other.countryRowid);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Name)) {
        this.Name = data().deepCopy(fields()[1].schema(), other.Name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.Type)) {
        this.Type = data().deepCopy(fields()[2].schema(), other.Type);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.GeographyCountryAlternateCodeDetails)) {
        this.GeographyCountryAlternateCodeDetails = data().deepCopy(fields()[3].schema(), other.GeographyCountryAlternateCodeDetails);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'countryRowid' field.
      * @return The value.
      */
    public java.lang.String getCountryRowid() {
      return countryRowid;
    }


    /**
      * Sets the value of the 'countryRowid' field.
      * @param value The value of 'countryRowid'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder setCountryRowid(java.lang.String value) {
      validate(fields()[0], value);
      this.countryRowid = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'countryRowid' field has been set.
      * @return True if the 'countryRowid' field has been set, false otherwise.
      */
    public boolean hasCountryRowid() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'countryRowid' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder clearCountryRowid() {
      countryRowid = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'Name' field.
      * @return The value.
      */
    public java.lang.String getName() {
      return Name;
    }


    /**
      * Sets the value of the 'Name' field.
      * @param value The value of 'Name'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder setName(java.lang.String value) {
      validate(fields()[1], value);
      this.Name = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'Name' field has been set.
      * @return True if the 'Name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'Name' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder clearName() {
      Name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'Type' field.
      * @return The value.
      */
    public java.lang.String getType() {
      return Type;
    }


    /**
      * Sets the value of the 'Type' field.
      * @param value The value of 'Type'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder setType(java.lang.String value) {
      validate(fields()[2], value);
      this.Type = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'Type' field has been set.
      * @return True if the 'Type' field has been set, false otherwise.
      */
    public boolean hasType() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'Type' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder clearType() {
      Type = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'GeographyCountryAlternateCodeDetails' field.
      * @return The value.
      */
    public java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> getGeographyCountryAlternateCodeDetails() {
      return GeographyCountryAlternateCodeDetails;
    }


    /**
      * Sets the value of the 'GeographyCountryAlternateCodeDetails' field.
      * @param value The value of 'GeographyCountryAlternateCodeDetails'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder setGeographyCountryAlternateCodeDetails(java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> value) {
      validate(fields()[3], value);
      this.GeographyCountryAlternateCodeDetails = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'GeographyCountryAlternateCodeDetails' field has been set.
      * @return True if the 'GeographyCountryAlternateCodeDetails' field has been set, false otherwise.
      */
    public boolean hasGeographyCountryAlternateCodeDetails() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'GeographyCountryAlternateCodeDetails' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryDetails.Builder clearGeographyCountryAlternateCodeDetails() {
      GeographyCountryAlternateCodeDetails = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GeographyCountryDetails build() {
      try {
        GeographyCountryDetails record = new GeographyCountryDetails();
        record.countryRowid = fieldSetFlags()[0] ? this.countryRowid : (java.lang.String) defaultValue(fields()[0]);
        record.Name = fieldSetFlags()[1] ? this.Name : (java.lang.String) defaultValue(fields()[1]);
        record.Type = fieldSetFlags()[2] ? this.Type : (java.lang.String) defaultValue(fields()[2]);
        record.GeographyCountryAlternateCodeDetails = fieldSetFlags()[3] ? this.GeographyCountryAlternateCodeDetails : (java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode>) defaultValue(fields()[3]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<GeographyCountryDetails>
    WRITER$ = (org.apache.avro.io.DatumWriter<GeographyCountryDetails>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<GeographyCountryDetails>
    READER$ = (org.apache.avro.io.DatumReader<GeographyCountryDetails>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    if (this.countryRowid == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.countryRowid);
    }

    if (this.Name == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.Name);
    }

    if (this.Type == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.Type);
    }

    if (this.GeographyCountryAlternateCodeDetails == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      long size0 = this.GeographyCountryAlternateCodeDetails.size();
      out.writeArrayStart();
      out.setItemCount(size0);
      long actualSize0 = 0;
      for (com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode e0: this.GeographyCountryAlternateCodeDetails) {
        actualSize0++;
        out.startItem();
        e0.customEncode(out);
      }
      out.writeArrayEnd();
      if (actualSize0 != size0)
        throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      if (in.readIndex() != 1) {
        in.readNull();
        this.countryRowid = null;
      } else {
        this.countryRowid = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.Name = null;
      } else {
        this.Name = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.Type = null;
      } else {
        this.Type = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.GeographyCountryAlternateCodeDetails = null;
      } else {
        long size0 = in.readArrayStart();
        java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> a0 = this.GeographyCountryAlternateCodeDetails;
        if (a0 == null) {
          a0 = new SpecificData.Array<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode>((int)size0, SCHEMA$.getField("GeographyCountryAlternateCodeDetails").schema().getTypes().get(1));
          this.GeographyCountryAlternateCodeDetails = a0;
        } else a0.clear();
        SpecificData.Array<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode>)a0 : null);
        for ( ; 0 < size0; size0 = in.arrayNext()) {
          for ( ; size0 != 0; size0--) {
            com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode e0 = (ga0 != null ? ga0.peek() : null);
            if (e0 == null) {
              e0 = new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode();
            }
            e0.customDecode(in);
            a0.add(e0);
          }
        }
      }

    } else {
      for (int i = 0; i < 4; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          if (in.readIndex() != 1) {
            in.readNull();
            this.countryRowid = null;
          } else {
            this.countryRowid = in.readString();
          }
          break;

        case 1:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Name = null;
          } else {
            this.Name = in.readString();
          }
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Type = null;
          } else {
            this.Type = in.readString();
          }
          break;

        case 3:
          if (in.readIndex() != 1) {
            in.readNull();
            this.GeographyCountryAlternateCodeDetails = null;
          } else {
            long size0 = in.readArrayStart();
            java.util.List<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> a0 = this.GeographyCountryAlternateCodeDetails;
            if (a0 == null) {
              a0 = new SpecificData.Array<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode>((int)size0, SCHEMA$.getField("GeographyCountryAlternateCodeDetails").schema().getTypes().get(1));
              this.GeographyCountryAlternateCodeDetails = a0;
            } else a0.clear();
            SpecificData.Array<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode>)a0 : null);
            for ( ; 0 < size0; size0 = in.arrayNext()) {
              for ( ; size0 != 0; size0--) {
                com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode e0 = (ga0 != null ? ga0.peek() : null);
                if (e0 == null) {
                  e0 = new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode();
                }
                e0.customDecode(in);
                a0.add(e0);
              }
            }
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









