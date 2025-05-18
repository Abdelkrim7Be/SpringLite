package com.bellagnech.springlite.di.xml;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * JAXB class to represent a property element in XML configuration.
 * Maps to the <property> element in XML.
 */
public class PropertyElement {
    
    private String name;
    private String value;
    private String ref;
    
    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlAttribute
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @XmlAttribute
    public String getRef() {
        return ref;
    }
    
    public void setRef(String ref) {
        this.ref = ref;
    }
}
