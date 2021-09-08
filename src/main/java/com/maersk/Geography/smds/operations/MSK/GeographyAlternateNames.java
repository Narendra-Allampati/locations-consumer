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

/** Geography AlternateNames details */
@org.apache.avro.specific.AvroGenerated
public class GeographyAlternateNames extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -6475794433160593217L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GeographyAlternateNames\",\"namespace\":\"com.maersk.Geography.smds.operations.MSK\",\"doc\":\"Geography AlternateNames details\",\"fields\":[{\"name\":\"Name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Description\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"Status\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<GeographyAlternateNames> ENCODER =
      new BinaryMessageEncoder<GeographyAlternateNames>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<GeographyAlternateNames> DECODER =
      new BinaryMessageDecoder<GeographyAlternateNames>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<GeographyAlternateNames> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<GeographyAlternateNames> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<GeographyAlternateNames> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<GeographyAlternateNames>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this GeographyAlternateNames to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a GeographyAlternateNames from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a GeographyAlternateNames instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static GeographyAlternateNames fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String Name;
   private java.lang.String Description;
   private java.lang.String Status;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public GeographyAlternateNames() {}

  /**
   * All-args constructor.
   * @param Name The new value for Name
   * @param Description The new value for Description
   * @param Status The new value for Status
   */
  public GeographyAlternateNames(java.lang.String Name, java.lang.String Description, java.lang.String Status) {
    this.Name = Name;
    this.Description = Description;
    this.Status = Status;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return Name;
    case 1: return Description;
    case 2: return Status;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: Name = value$ != null ? value$.toString() : null; break;
    case 1: Description = value$ != null ? value$.toString() : null; break;
    case 2: Status = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
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
   * Gets the value of the 'Description' field.
   * @return The value of the 'Description' field.
   */
  public java.lang.String getDescription() {
    return Description;
  }


  /**
   * Sets the value of the 'Description' field.
   * @param value the value to set.
   */
  public void setDescription(java.lang.String value) {
    this.Description = value;
  }

  /**
   * Gets the value of the 'Status' field.
   * @return The value of the 'Status' field.
   */
  public java.lang.String getStatus() {
    return Status;
  }


  /**
   * Sets the value of the 'Status' field.
   * @param value the value to set.
   */
  public void setStatus(java.lang.String value) {
    this.Status = value;
  }

  /**
   * Creates a new GeographyAlternateNames RecordBuilder.
   * @return A new GeographyAlternateNames RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder newBuilder() {
    return new com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder();
  }

  /**
   * Creates a new GeographyAlternateNames RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new GeographyAlternateNames RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder(other);
    }
  }

  /**
   * Creates a new GeographyAlternateNames RecordBuilder by copying an existing GeographyAlternateNames instance.
   * @param other The existing instance to copy.
   * @return A new GeographyAlternateNames RecordBuilder
   */
  public static com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder newBuilder(com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames other) {
    if (other == null) {
      return new com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder();
    } else {
      return new com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder(other);
    }
  }

  /**
   * RecordBuilder for GeographyAlternateNames instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GeographyAlternateNames>
    implements org.apache.avro.data.RecordBuilder<GeographyAlternateNames> {

    private java.lang.String Name;
    private java.lang.String Description;
    private java.lang.String Status;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.Name)) {
        this.Name = data().deepCopy(fields()[0].schema(), other.Name);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.Description)) {
        this.Description = data().deepCopy(fields()[1].schema(), other.Description);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.Status)) {
        this.Status = data().deepCopy(fields()[2].schema(), other.Status);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing GeographyAlternateNames instance
     * @param other The existing instance to copy.
     */
    private Builder(com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.Name)) {
        this.Name = data().deepCopy(fields()[0].schema(), other.Name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Description)) {
        this.Description = data().deepCopy(fields()[1].schema(), other.Description);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.Status)) {
        this.Status = data().deepCopy(fields()[2].schema(), other.Status);
        fieldSetFlags()[2] = true;
      }
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
    public com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder setName(java.lang.String value) {
      validate(fields()[0], value);
      this.Name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'Name' field has been set.
      * @return True if the 'Name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'Name' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder clearName() {
      Name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'Description' field.
      * @return The value.
      */
    public java.lang.String getDescription() {
      return Description;
    }


    /**
      * Sets the value of the 'Description' field.
      * @param value The value of 'Description'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder setDescription(java.lang.String value) {
      validate(fields()[1], value);
      this.Description = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'Description' field has been set.
      * @return True if the 'Description' field has been set, false otherwise.
      */
    public boolean hasDescription() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'Description' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder clearDescription() {
      Description = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'Status' field.
      * @return The value.
      */
    public java.lang.String getStatus() {
      return Status;
    }


    /**
      * Sets the value of the 'Status' field.
      * @param value The value of 'Status'.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder setStatus(java.lang.String value) {
      validate(fields()[2], value);
      this.Status = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'Status' field has been set.
      * @return True if the 'Status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'Status' field.
      * @return This builder.
      */
    public com.maersk.Geography.smds.operations.MSK.GeographyAlternateNames.Builder clearStatus() {
      Status = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GeographyAlternateNames build() {
      try {
        GeographyAlternateNames record = new GeographyAlternateNames();
        record.Name = fieldSetFlags()[0] ? this.Name : (java.lang.String) defaultValue(fields()[0]);
        record.Description = fieldSetFlags()[1] ? this.Description : (java.lang.String) defaultValue(fields()[1]);
        record.Status = fieldSetFlags()[2] ? this.Status : (java.lang.String) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<GeographyAlternateNames>
    WRITER$ = (org.apache.avro.io.DatumWriter<GeographyAlternateNames>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<GeographyAlternateNames>
    READER$ = (org.apache.avro.io.DatumReader<GeographyAlternateNames>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    if (this.Name == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.Name);
    }

    if (this.Description == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.Description);
    }

    if (this.Status == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.Status);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      if (in.readIndex() != 1) {
        in.readNull();
        this.Name = null;
      } else {
        this.Name = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.Description = null;
      } else {
        this.Description = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.Status = null;
      } else {
        this.Status = in.readString();
      }

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Name = null;
          } else {
            this.Name = in.readString();
          }
          break;

        case 1:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Description = null;
          } else {
            this.Description = in.readString();
          }
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.Status = null;
          } else {
            this.Status = in.readString();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










