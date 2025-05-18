package com.bellagnech.springlite.di;

/**
 * Holder for a property value, typically configured via XML.
 * Contains the property name and either a value or a reference to another bean.
 */
public class PropertyValue {
    private final String name;
    private final Object value;
    private final boolean isRef;

    /**
     * Create a new property value with a primitive/string value.
     * 
     * @param name the name of the property
     * @param value the value of the property
     */
    public PropertyValue(String name, Object value) {
        this(name, value, false);
    }

    /**
     * Create a new property value.
     * 
     * @param name the name of the property
     * @param value the value of the property or bean reference name
     * @param isRef whether this is a reference to another bean
     */
    public PropertyValue(String name, Object value, boolean isRef) {
        this.name = name;
        this.value = value;
        this.isRef = isRef;
    }

    /**
     * Get the name of the property.
     * 
     * @return the property name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of the property.
     * 
     * @return the property value or bean reference name
     */
    public Object getValue() {
        return value;
    }

    /**
     * Check if this property is a reference to another bean.
     * 
     * @return true if this is a reference to another bean
     */
    public boolean isRef() {
        return isRef;
    }
}
