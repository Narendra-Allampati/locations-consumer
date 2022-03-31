/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.maersk.facility.smds.operations.msk;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

/** Parent AlternateCode details */
@org.apache.avro.specific.AvroGenerated
public class parentAlternateCode extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -3938612458371987060L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"parentAlternateCode\",\"namespace\":\"com.maersk.facility.smds.operations.msk\",\"doc\":\"Parent AlternateCode details\",\"fields\":[{\"name\":\"codeType\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"The alternate code type of  parent object. Example: GEOID\"},{\"name\":\"code\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"The alternate code value of  parent object. Example: 0AEJZHTGIMJK0\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<parentAlternateCode> ENCODER =
      new BinaryMessageEncoder<parentAlternateCode>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<parentAlternateCode> DECODER =
      new BinaryMessageDecoder<parentAlternateCode>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<parentAlternateCode> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<parentAlternateCode> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<parentAlternateCode> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<parentAlternateCode>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this parentAlternateCode to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a parentAlternateCode from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a parentAlternateCode instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static parentAlternateCode fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** The alternate code type of  parent object. Example: GEOID */
  private java.lang.String codeType;
  /** The alternate code value of  parent object. Example: 0AEJZHTGIMJK0 */
  private java.lang.String code;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public parentAlternateCode() {}

  /**
   * All-args constructor.
   * @param codeType The alternate code type of  parent object. Example: GEOID
   * @param code The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
   */
  public parentAlternateCode(java.lang.String codeType, java.lang.String code) {
    this.codeType = codeType;
    this.code = code;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return codeType;
    case 1: return code;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: codeType = value$ != null ? value$.toString() : null; break;
    case 1: code = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'codeType' field.
   * @return The alternate code type of  parent object. Example: GEOID
   */
  public java.lang.String getCodeType() {
    return codeType;
  }


  /**
   * Sets the value of the 'codeType' field.
   * The alternate code type of  parent object. Example: GEOID
   * @param value the value to set.
   */
  public void setCodeType(java.lang.String value) {
    this.codeType = value;
  }

  /**
   * Gets the value of the 'code' field.
   * @return The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
   */
  public java.lang.String getCode() {
    return code;
  }


  /**
   * Sets the value of the 'code' field.
   * The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
   * @param value the value to set.
   */
  public void setCode(java.lang.String value) {
    this.code = value;
  }

  /**
   * Creates a new parentAlternateCode RecordBuilder.
   * @return A new parentAlternateCode RecordBuilder
   */
  public static com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder newBuilder() {
    return new com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder();
  }

  /**
   * Creates a new parentAlternateCode RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new parentAlternateCode RecordBuilder
   */
  public static com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder newBuilder(com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder other) {
    if (other == null) {
      return new com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder();
    } else {
      return new com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder(other);
    }
  }

  /**
   * Creates a new parentAlternateCode RecordBuilder by copying an existing parentAlternateCode instance.
   * @param other The existing instance to copy.
   * @return A new parentAlternateCode RecordBuilder
   */
  public static com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder newBuilder(com.maersk.facility.smds.operations.msk.parentAlternateCode other) {
    if (other == null) {
      return new com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder();
    } else {
      return new com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder(other);
    }
  }

  /**
   * RecordBuilder for parentAlternateCode instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<parentAlternateCode>
    implements org.apache.avro.data.RecordBuilder<parentAlternateCode> {

    /** The alternate code type of  parent object. Example: GEOID */
    private java.lang.String codeType;
    /** The alternate code value of  parent object. Example: 0AEJZHTGIMJK0 */
    private java.lang.String code;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.codeType)) {
        this.codeType = data().deepCopy(fields()[0].schema(), other.codeType);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.code)) {
        this.code = data().deepCopy(fields()[1].schema(), other.code);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing parentAlternateCode instance
     * @param other The existing instance to copy.
     */
    private Builder(com.maersk.facility.smds.operations.msk.parentAlternateCode other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.codeType)) {
        this.codeType = data().deepCopy(fields()[0].schema(), other.codeType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.code)) {
        this.code = data().deepCopy(fields()[1].schema(), other.code);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'codeType' field.
      * The alternate code type of  parent object. Example: GEOID
      * @return The value.
      */
    public java.lang.String getCodeType() {
      return codeType;
    }


    /**
      * Sets the value of the 'codeType' field.
      * The alternate code type of  parent object. Example: GEOID
      * @param value The value of 'codeType'.
      * @return This builder.
      */
    public com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder setCodeType(java.lang.String value) {
      validate(fields()[0], value);
      this.codeType = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'codeType' field has been set.
      * The alternate code type of  parent object. Example: GEOID
      * @return True if the 'codeType' field has been set, false otherwise.
      */
    public boolean hasCodeType() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'codeType' field.
      * The alternate code type of  parent object. Example: GEOID
      * @return This builder.
      */
    public com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder clearCodeType() {
      codeType = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'code' field.
      * The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
      * @return The value.
      */
    public java.lang.String getCode() {
      return code;
    }


    /**
      * Sets the value of the 'code' field.
      * The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
      * @param value The value of 'code'.
      * @return This builder.
      */
    public com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder setCode(java.lang.String value) {
      validate(fields()[1], value);
      this.code = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'code' field has been set.
      * The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
      * @return True if the 'code' field has been set, false otherwise.
      */
    public boolean hasCode() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'code' field.
      * The alternate code value of  parent object. Example: 0AEJZHTGIMJK0
      * @return This builder.
      */
    public com.maersk.facility.smds.operations.msk.parentAlternateCode.Builder clearCode() {
      code = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public parentAlternateCode build() {
      try {
        parentAlternateCode record = new parentAlternateCode();
        record.codeType = fieldSetFlags()[0] ? this.codeType : (java.lang.String) defaultValue(fields()[0]);
        record.code = fieldSetFlags()[1] ? this.code : (java.lang.String) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<parentAlternateCode>
    WRITER$ = (org.apache.avro.io.DatumWriter<parentAlternateCode>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<parentAlternateCode>
    READER$ = (org.apache.avro.io.DatumReader<parentAlternateCode>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.codeType);

    out.writeString(this.code);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.codeType = in.readString();

      this.code = in.readString();

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.codeType = in.readString();
          break;

        case 1:
          this.code = in.readString();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










