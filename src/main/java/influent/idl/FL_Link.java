/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package influent.idl;  
@SuppressWarnings("all")
/** * The links in the social, financial, communications or other graphs. May represent communication events, financial transactions
	 * or social connections. */
@org.apache.avro.specific.AvroGenerated
public class FL_Link extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"FL_Link\",\"namespace\":\"influent.idl\",\"doc\":\"* The links in the social, financial, communications or other graphs. May represent communication events, financial transactions\\r\\n\\t * or social connections.\",\"fields\":[{\"name\":\"tags\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"enum\",\"name\":\"FL_LinkTag\",\"doc\":\"This is the current list of tags for Links:\",\"symbols\":[\"FINANCIAL\",\"SOCIAL\",\"COMMUNICATION\",\"OTHER\"]}},\"doc\":\"Link Tags (see above, e.g. \\\"FINANCIAL\\\")\"},{\"name\":\"source\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"source entity uid\",\"default\":null},{\"name\":\"target\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"target entity uid\",\"default\":null},{\"name\":\"directed\",\"type\":\"boolean\",\"doc\":\"true if directed, false if undirected\",\"default\":true},{\"name\":\"provenance\",\"type\":[{\"type\":\"record\",\"name\":\"FL_Provenance\",\"doc\":\"This is a placeholder for future modeling of provenance. It is not a required field in any service calls.\",\"fields\":[{\"name\":\"uri\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"Placeholder for now. Express provenance as a single URI.\"}]},\"null\"],\"default\":null},{\"name\":\"uncertainty\",\"type\":[{\"type\":\"record\",\"name\":\"FL_Uncertainty\",\"doc\":\"This is a placeholder for future modeling of uncertainty. It is not a required field in any service calls.\",\"fields\":[{\"name\":\"confidence\",\"type\":\"double\",\"doc\":\"Placeholder for now. Express uncertainty as a single number from 0 to 1.\",\"default\":1}]},\"null\"],\"default\":null},{\"name\":\"properties\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"FL_Property\",\"doc\":\"Each property on an Entity or Link is a name-value pair, with data type information, as well as optional\\r\\n\\t provenance. Tags provide a way for the data provider to associate semantic annotations to each property\\r\\n\\t in terms of the semantics of the application.  \\r\\n\\t \\r\\n\\t CHANGED IN 1.5\",\"fields\":[{\"name\":\"key\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"the field name in the underlying data source\"},{\"name\":\"friendlyText\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"user-friendly short-text for key (displayable)\",\"default\":null},{\"name\":\"range\",\"type\":[{\"type\":\"record\",\"name\":\"FL_SingletonRange\",\"doc\":\"Single value\\r\\n\\t\\r\\n\\tADDED IN 1.5\",\"fields\":[{\"name\":\"value\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",{\"type\":\"record\",\"name\":\"FL_GeoData\",\"doc\":\"Structured representation of geo-spatial data.\",\"fields\":[{\"name\":\"text\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"an address or other place reference; unstructured text field\",\"default\":null},{\"name\":\"lat\",\"type\":[\"double\",\"null\"],\"doc\":\"latitude\",\"default\":null},{\"name\":\"lon\",\"type\":[\"double\",\"null\"],\"doc\":\"longitude\",\"default\":null},{\"name\":\"cc\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"ISO 3 digit country code\",\"default\":null}]}]},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"FL_PropertyType\",\"doc\":\"Allowed types for Property values.\\r\\n\\r\\n\\t CHANGED in 1.5\",\"symbols\":[\"DOUBLE\",\"LONG\",\"BOOLEAN\",\"STRING\",\"DATE\",\"GEO\",\"OTHER\"]},\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},{\"type\":\"record\",\"name\":\"FL_ListRange\",\"doc\":\"List of values\\r\\n\\t\\r\\n\\tADDED IN 1.5\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"FL_GeoData\"]}},{\"name\":\"type\",\"type\":\"FL_PropertyType\",\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},{\"type\":\"record\",\"name\":\"FL_BoundedRange\",\"doc\":\"Bounded or unbounded range values\\r\\n\\t\\r\\n\\tADDED IN 1.5\",\"fields\":[{\"name\":\"start\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"FL_GeoData\",\"null\"],\"doc\":\"start of range, or null if unbounded start\"},{\"name\":\"end\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"FL_GeoData\",\"null\"],\"doc\":\"end of range, or null if unbounded start\"},{\"name\":\"inclusive\",\"type\":\"boolean\",\"doc\":\"If true, range includes specified endpoint. If false, range is exclusive.\"},{\"name\":\"type\",\"type\":\"FL_PropertyType\",\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]}],\"doc\":\"range of values\",\"default\":null},{\"name\":\"provenance\",\"type\":[\"FL_Provenance\",\"null\"],\"default\":null},{\"name\":\"uncertainty\",\"type\":[\"FL_Uncertainty\",\"null\"],\"default\":null},{\"name\":\"tags\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"enum\",\"name\":\"FL_PropertyTag\",\"doc\":\"Tags are defined by the application layer as a taxonomy of user and application concepts,\\r\\n\\t independent of the data sources. This allows application semantics to be re-used with new\\r\\n\\t data, with a minimum of new software design and development. Data layer entity types, link\\r\\n\\t types and properties should be mapped into the list of tags. The application layer must be\\r\\n\\t able to search by native field name or by tag interchangeably, and properties returned must\\r\\n\\t contain both native field names as well as tags.\\r\\n\\t \\r\\n\\t The list of tags may change as application features evolve, though that will require\\r\\n\\t collaboration with the data layer providers. Evolving the tag list should not change the\\r\\n\\t Data Access or Search APIs.\\r\\n\\r\\n\\t This is the current list of tags for Properties:\\r\\n\\t \\r\\n\\t CHANGED in 1.5:\\r\\n\\t   - CREDIT/DEBIT changed to INFLOWING/OUTFLOWING\\r\\n\\t   - added USD\\r\\n\\t   - added DURATION\",\"symbols\":[\"ID\",\"TYPE\",\"NAME\",\"LABEL\",\"STAT\",\"TEXT\",\"STATUS\",\"ANNOTATION\",\"WARNING\",\"LINKED_DATA\",\"IMAGE\",\"GEO\",\"DATE\",\"AMOUNT\",\"INFLOWING\",\"OUTFLOWING\",\"COUNT\",\"SERIES\",\"CONSTRUCTED\",\"RAW\",\"USD\",\"DURATION\"]}},\"doc\":\"one or more tags from the Tag list, used to map this source-specific field into the semantics of applications\"}]}}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** Link Tags (see above, e.g. "FINANCIAL") */
   private java.util.List<influent.idl.FL_LinkTag> tags;
  /** source entity uid */
   private java.lang.String source;
  /** target entity uid */
   private java.lang.String target;
  /** true if directed, false if undirected */
   private boolean directed;
   private influent.idl.FL_Provenance provenance;
   private influent.idl.FL_Uncertainty uncertainty;
   private java.util.List<influent.idl.FL_Property> properties;

  /**
   * Default constructor.
   */
  public FL_Link() {}

  /**
   * All-args constructor.
   */
  public FL_Link(java.util.List<influent.idl.FL_LinkTag> tags, java.lang.String source, java.lang.String target, java.lang.Boolean directed, influent.idl.FL_Provenance provenance, influent.idl.FL_Uncertainty uncertainty, java.util.List<influent.idl.FL_Property> properties) {
    this.tags = tags;
    this.source = source;
    this.target = target;
    this.directed = directed;
    this.provenance = provenance;
    this.uncertainty = uncertainty;
    this.properties = properties;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return tags;
    case 1: return source;
    case 2: return target;
    case 3: return directed;
    case 4: return provenance;
    case 5: return uncertainty;
    case 6: return properties;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: tags = (java.util.List<influent.idl.FL_LinkTag>)value$; break;
    case 1: source = (java.lang.String)value$; break;
    case 2: target = (java.lang.String)value$; break;
    case 3: directed = (java.lang.Boolean)value$; break;
    case 4: provenance = (influent.idl.FL_Provenance)value$; break;
    case 5: uncertainty = (influent.idl.FL_Uncertainty)value$; break;
    case 6: properties = (java.util.List<influent.idl.FL_Property>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'tags' field.
   * Link Tags (see above, e.g. "FINANCIAL")   */
  public java.util.List<influent.idl.FL_LinkTag> getTags() {
    return tags;
  }

  /**
   * Sets the value of the 'tags' field.
   * Link Tags (see above, e.g. "FINANCIAL")   * @param value the value to set.
   */
  public void setTags(java.util.List<influent.idl.FL_LinkTag> value) {
    this.tags = value;
  }

  /**
   * Gets the value of the 'source' field.
   * source entity uid   */
  public java.lang.String getSource() {
    return source;
  }

  /**
   * Sets the value of the 'source' field.
   * source entity uid   * @param value the value to set.
   */
  public void setSource(java.lang.String value) {
    this.source = value;
  }

  /**
   * Gets the value of the 'target' field.
   * target entity uid   */
  public java.lang.String getTarget() {
    return target;
  }

  /**
   * Sets the value of the 'target' field.
   * target entity uid   * @param value the value to set.
   */
  public void setTarget(java.lang.String value) {
    this.target = value;
  }

  /**
   * Gets the value of the 'directed' field.
   * true if directed, false if undirected   */
  public java.lang.Boolean getDirected() {
    return directed;
  }

  /**
   * Sets the value of the 'directed' field.
   * true if directed, false if undirected   * @param value the value to set.
   */
  public void setDirected(java.lang.Boolean value) {
    this.directed = value;
  }

  /**
   * Gets the value of the 'provenance' field.
   */
  public influent.idl.FL_Provenance getProvenance() {
    return provenance;
  }

  /**
   * Sets the value of the 'provenance' field.
   * @param value the value to set.
   */
  public void setProvenance(influent.idl.FL_Provenance value) {
    this.provenance = value;
  }

  /**
   * Gets the value of the 'uncertainty' field.
   */
  public influent.idl.FL_Uncertainty getUncertainty() {
    return uncertainty;
  }

  /**
   * Sets the value of the 'uncertainty' field.
   * @param value the value to set.
   */
  public void setUncertainty(influent.idl.FL_Uncertainty value) {
    this.uncertainty = value;
  }

  /**
   * Gets the value of the 'properties' field.
   */
  public java.util.List<influent.idl.FL_Property> getProperties() {
    return properties;
  }

  /**
   * Sets the value of the 'properties' field.
   * @param value the value to set.
   */
  public void setProperties(java.util.List<influent.idl.FL_Property> value) {
    this.properties = value;
  }

  /** Creates a new FL_Link RecordBuilder */
  public static influent.idl.FL_Link.Builder newBuilder() {
    return new influent.idl.FL_Link.Builder();
  }
  
  /** Creates a new FL_Link RecordBuilder by copying an existing Builder */
  public static influent.idl.FL_Link.Builder newBuilder(influent.idl.FL_Link.Builder other) {
    return new influent.idl.FL_Link.Builder(other);
  }
  
  /** Creates a new FL_Link RecordBuilder by copying an existing FL_Link instance */
  public static influent.idl.FL_Link.Builder newBuilder(influent.idl.FL_Link other) {
    return new influent.idl.FL_Link.Builder(other);
  }
  
  /**
   * RecordBuilder for FL_Link instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<FL_Link>
    implements org.apache.avro.data.RecordBuilder<FL_Link> {

    private java.util.List<influent.idl.FL_LinkTag> tags;
    private java.lang.String source;
    private java.lang.String target;
    private boolean directed;
    private influent.idl.FL_Provenance provenance;
    private influent.idl.FL_Uncertainty uncertainty;
    private java.util.List<influent.idl.FL_Property> properties;

    /** Creates a new Builder */
    private Builder() {
      super(influent.idl.FL_Link.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(influent.idl.FL_Link.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing FL_Link instance */
    private Builder(influent.idl.FL_Link other) {
            super(influent.idl.FL_Link.SCHEMA$);
      if (isValidValue(fields()[0], other.tags)) {
        this.tags = data().deepCopy(fields()[0].schema(), other.tags);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.source)) {
        this.source = data().deepCopy(fields()[1].schema(), other.source);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.target)) {
        this.target = data().deepCopy(fields()[2].schema(), other.target);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.directed)) {
        this.directed = data().deepCopy(fields()[3].schema(), other.directed);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.provenance)) {
        this.provenance = data().deepCopy(fields()[4].schema(), other.provenance);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.uncertainty)) {
        this.uncertainty = data().deepCopy(fields()[5].schema(), other.uncertainty);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.properties)) {
        this.properties = data().deepCopy(fields()[6].schema(), other.properties);
        fieldSetFlags()[6] = true;
      }
    }

    /** Gets the value of the 'tags' field */
    public java.util.List<influent.idl.FL_LinkTag> getTags() {
      return tags;
    }
    
    /** Sets the value of the 'tags' field */
    public influent.idl.FL_Link.Builder setTags(java.util.List<influent.idl.FL_LinkTag> value) {
      validate(fields()[0], value);
      this.tags = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'tags' field has been set */
    public boolean hasTags() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'tags' field */
    public influent.idl.FL_Link.Builder clearTags() {
      tags = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'source' field */
    public java.lang.String getSource() {
      return source;
    }
    
    /** Sets the value of the 'source' field */
    public influent.idl.FL_Link.Builder setSource(java.lang.String value) {
      validate(fields()[1], value);
      this.source = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'source' field has been set */
    public boolean hasSource() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'source' field */
    public influent.idl.FL_Link.Builder clearSource() {
      source = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'target' field */
    public java.lang.String getTarget() {
      return target;
    }
    
    /** Sets the value of the 'target' field */
    public influent.idl.FL_Link.Builder setTarget(java.lang.String value) {
      validate(fields()[2], value);
      this.target = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'target' field has been set */
    public boolean hasTarget() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'target' field */
    public influent.idl.FL_Link.Builder clearTarget() {
      target = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'directed' field */
    public java.lang.Boolean getDirected() {
      return directed;
    }
    
    /** Sets the value of the 'directed' field */
    public influent.idl.FL_Link.Builder setDirected(boolean value) {
      validate(fields()[3], value);
      this.directed = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'directed' field has been set */
    public boolean hasDirected() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'directed' field */
    public influent.idl.FL_Link.Builder clearDirected() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'provenance' field */
    public influent.idl.FL_Provenance getProvenance() {
      return provenance;
    }
    
    /** Sets the value of the 'provenance' field */
    public influent.idl.FL_Link.Builder setProvenance(influent.idl.FL_Provenance value) {
      validate(fields()[4], value);
      this.provenance = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'provenance' field has been set */
    public boolean hasProvenance() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'provenance' field */
    public influent.idl.FL_Link.Builder clearProvenance() {
      provenance = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'uncertainty' field */
    public influent.idl.FL_Uncertainty getUncertainty() {
      return uncertainty;
    }
    
    /** Sets the value of the 'uncertainty' field */
    public influent.idl.FL_Link.Builder setUncertainty(influent.idl.FL_Uncertainty value) {
      validate(fields()[5], value);
      this.uncertainty = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'uncertainty' field has been set */
    public boolean hasUncertainty() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'uncertainty' field */
    public influent.idl.FL_Link.Builder clearUncertainty() {
      uncertainty = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'properties' field */
    public java.util.List<influent.idl.FL_Property> getProperties() {
      return properties;
    }
    
    /** Sets the value of the 'properties' field */
    public influent.idl.FL_Link.Builder setProperties(java.util.List<influent.idl.FL_Property> value) {
      validate(fields()[6], value);
      this.properties = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'properties' field has been set */
    public boolean hasProperties() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'properties' field */
    public influent.idl.FL_Link.Builder clearProperties() {
      properties = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public FL_Link build() {
      try {
        FL_Link record = new FL_Link();
        record.tags = fieldSetFlags()[0] ? this.tags : (java.util.List<influent.idl.FL_LinkTag>) defaultValue(fields()[0]);
        record.source = fieldSetFlags()[1] ? this.source : (java.lang.String) defaultValue(fields()[1]);
        record.target = fieldSetFlags()[2] ? this.target : (java.lang.String) defaultValue(fields()[2]);
        record.directed = fieldSetFlags()[3] ? this.directed : (java.lang.Boolean) defaultValue(fields()[3]);
        record.provenance = fieldSetFlags()[4] ? this.provenance : (influent.idl.FL_Provenance) defaultValue(fields()[4]);
        record.uncertainty = fieldSetFlags()[5] ? this.uncertainty : (influent.idl.FL_Uncertainty) defaultValue(fields()[5]);
        record.properties = fieldSetFlags()[6] ? this.properties : (java.util.List<influent.idl.FL_Property>) defaultValue(fields()[6]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
