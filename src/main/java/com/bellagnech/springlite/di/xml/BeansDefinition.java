package com.bellagnech.springlite.di.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JAXB class to represent the root element of beans XML configuration.
 * Maps to the <beans> element in XML.
 */
@XmlRootElement(name = "beans")
public class BeansDefinition {
    
    private List<BeanElement> beans = new ArrayList<>();
    
    @XmlElement(name = "bean")
    public List<BeanElement> getBeans() {
        return beans;
    }
    
    public void setBeans(List<BeanElement> beans) {
        this.beans = beans;
    }
}
