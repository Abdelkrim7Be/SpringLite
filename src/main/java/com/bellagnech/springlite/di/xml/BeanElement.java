package com.bellagnech.springlite.di.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a bean element in XML configuration.
 * Maps to the <bean> element in XML.
 */
public class BeanElement {
    
    private String id;
    private String className;
    private String scope = "singleton";
    private List<PropertyElement> properties = new ArrayList<>();
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public List<PropertyElement> getProperties() {
        return properties;
    }
    
    public void setProperties(List<PropertyElement> properties) {
        this.properties = properties;
    }
}
