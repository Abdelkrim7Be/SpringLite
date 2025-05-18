package com.bellagnech.springlite.test;

public class PrototypeBean {
    private static int instanceCount = 0;
    private final int instanceNumber;
    
    public PrototypeBean() {
        instanceNumber = ++instanceCount;
        System.out.println("PrototypeBean constructor called - instance #" + instanceNumber);
    }
    
    public int getInstanceNumber() {
        return instanceNumber;
    }
    
    @Override
    public String toString() {
        return "PrototypeBean [instanceNumber=" + instanceNumber + "]";
    }
}
