package com.bellagnech.springlite.test;

public class ValueBean {
    private String stringValue;
    private int intValue;
    
    public ValueBean() {
        System.out.println("ValueBean constructor called");
    }
    
    public String getStringValue() {
        return stringValue;
    }
    
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
    
    public int getIntValue() {
        return intValue;
    }
    
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    
    @Override
    public String toString() {
        return "ValueBean [stringValue=" + stringValue + ", intValue=" + intValue + "]";
    }
}
