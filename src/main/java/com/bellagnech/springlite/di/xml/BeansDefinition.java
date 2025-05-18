package com.bellagnech.springlite.di.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the root element of beans XML configuration.
 * Maps to the <beans> element in XML.
 */
public class BeansDefinition {
    
    private List<BeanElement> beans = new ArrayList<>();
    
    public List<BeanElement> getBeans() {
        return beans;
    }
    
    public void setBeans(List<BeanElement> beans) {
        this.beans = beans;
    }
}
