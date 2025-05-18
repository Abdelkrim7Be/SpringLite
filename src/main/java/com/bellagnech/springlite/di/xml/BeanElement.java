package com.bellagnech.springlite.di.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JAXB class to represent a bean element in XML configuration.
 * Maps to the <bean> element in XML.
 */
public class BeanElement {
    
    private String id;
    private String className;
    private String scope = "singleton";
    private List<PropertyElement> properties = new ArrayList<>();
    
    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @XmlAttribute(name = "class", required = true)
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    @XmlAttribute
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    @XmlElement(name = "property")
    public List<PropertyElement> getProperties() {
        return properties;
    }
    
    public void setProperties(List<PropertyElement> properties) {
        this.properties = properties;
    }
}
