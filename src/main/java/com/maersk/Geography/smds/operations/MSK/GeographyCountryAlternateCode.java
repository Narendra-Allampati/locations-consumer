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

/** Geography Country AlternateCode details */
@org.apache.avro.specific.AvroGenerated
public class GeographyCountryAlternateCode extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 5522231233062467384L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GeographyCountryAlternateCode\",\"namespace\":\"com.maersk.Geography.smds.operations.MSK\",\"doc\":\"Geography Country AlternateCode details\",\"fields\":[{\"name\":\"CodeType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Code\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<GeographyCountryAlternateCode> ENCODER =
      new BinaryMessageEncoder<GeographyCountryAlternateCode>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<GeographyCountryAlternateCode> DECODER =
      new BinaryMessageDecoder<GeographyCountryAlternateCode>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<GeographyCountryAlternateCode> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<GeographyCountryAlternateCode> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<GeographyCountryAlternateCode> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<GeographyCountryAlternateCode>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this GeographyCountryAlternateCode to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a GeographyCountryAlternateCode from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a GeographyCountryAlternateCode instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static GeographyCountryAlternateCode fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String CodeType;
   private java.lang.String Code;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public GeographyCountryAlternateCode() {}

  /**
   * All-args constructor.
   * @param CodeType The new value for CodeType
   * @param Code The new value for Code
   */
  public GeographyCountryAlternateCode(java.lang.String CodeType, java.lang.String Code) {
    this.CodeType = CodeType;
    this.Code = Code;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return CodeType;
    case 1: return Code;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: CodeType = value$ != null ? value$.toString() : null; break;
    case 1: Code = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'CodeType' field.
   * @return The value of the 'CodeType' field.
   */
  public java.lang.String getCodeType() {
    return CodeType;
  }


  /**
   * Sets the value of the 'CodeType' field.
   * @param value the value to set.
   */
  public void setCodeType(java.lang.String value) {
    this.CodeType = value;
  }

  /**
   * Gets the value of the 'Code' field.
   * @return The value of the 'Code' field.
   */
  public java.lang.String getCode() {
    return Code;
  }


  /**
   * Sets the value of the 'Code' field.
   * @param value the value to set.
   */
  public void setCode(java.lang.String value) {
    this.Code = value;
  }

  /**
   * Creates a new GeographyCountryAlternateCode RecordBuilder.
   * @return A new GeographyCountryAlternateCode RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder newBuilder() {
    return new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder();
  }

  /**
   * Creates a new GeographyCountryAlternateCode RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new GeographyCountryAlternateCode RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder(other);
    }
  }

  /**
   * Creates a new GeographyCountryAlternateCode RecordBuilder by copying an existing GeographyCountryAlternateCode instance.
   * @param other The existing instance to copy.
   * @return A new GeographyCountryAlternateCode RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder(other);
    }
  }

  /**
   * RecordBuilder for GeographyCountryAlternateCode instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GeographyCountryAlternateCode>
    implements org.apache.avro.data.RecordBuilder<GeographyCountryAlternateCode> {

    private java.lang.String CodeType;
    private java.lang.String Code;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.CodeType)) {
        this.CodeType = data().deepCopy(fields()[0].schema(), other.CodeType);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.Code)) {
        this.Code = data().deepCopy(fields()[1].schema(), other.Code);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing GeographyCountryAlternateCode instance
     * @param other The existing instance to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.CodeType)) {
        this.CodeType = data().deepCopy(fields()[0].schema(), other.CodeType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Code)) {
        this.Code = data().deepCopy(fields()[1].schema(), other.Code);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'CodeType' field.
      * @return The value.
      */
    public java.lang.String getCodeType() {
      return CodeType;
    }


    /**
      * Sets the value of the 'CodeType' field.
      * @param value The value of 'CodeType'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder setCodeType(java.lang.String value) {
      validate(fields()[0], value);
      this.CodeType = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'CodeType' field has been set.
      * @return True if the 'CodeType' field has been set, false otherwise.
      */
    public boolean hasCodeType() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'CodeType' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder clearCodeType() {
      CodeType = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'Code' field.
      * @return The value.
      */
    public java.lang.String getCode() {
      return Code;
    }


    /**
      * Sets the value of the 'Code' field.
      * @param value The value of 'Code'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder setCode(java.lang.String value) {
      validate(fields()[1], value);
      this.Code = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'Code' field has been set.
      * @return True if the 'Code' field has been set, false otherwise.
      */
    public boolean hasCode() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'Code' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyCountryAlternateCode.Builder clearCode() {
      Code = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GeographyCountryAlternateCode build() {
      try {
        GeographyCountryAlternateCode record = new GeographyCountryAlternateCode();
        record.CodeType = fieldSetFlags()[0] ? this.CodeType : (java.lang.String) defaultValue(fields()[0]);
        record.Code = fieldSetFlags()[1] ? this.Code : (java.lang.String) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<GeographyCountryAlternateCode>
    WRITER$ = (org.apache.avro.io.DatumWriter<GeographyCountryAlternateCode>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<GeographyCountryAlternateCode>
    READER$ = (org.apache.avro.io.DatumReader<GeographyCountryAlternateCode>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    if (this.CodeType == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.CodeType);
    }

    if (this.Code == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.Code);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      if (in.readIndex() != 1) {
        in.readNull();
        this.CodeType = null;
      } else {
        this.CodeType = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.Code = null;
      } else {
        this.Code = in.readString();
      }

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          if (in.readIndex() != 1) {
            in.readNull();
            this.CodeType = null;
          } else {
            this.CodeType = in.readString();
          }
          break;

        case 1:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Code = null;
          } else {
            this.Code = in.readString();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










