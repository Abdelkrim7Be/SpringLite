package com.bellagnech.springlite.di.xml;

/**
 * Class to represent a property element in XML configuration.
 * Maps to the <property> element in XML.
 */
public class PropertyElement {
    
    private String name;
    private String value;
    private String ref;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getRef() {
        return ref;
    }
    
    public void setRef(String ref) {
        this.ref = ref;
    }
}
