package com.bellagnech.springlite.test;

public class FieldInjectionBean {
    private SimpleBean dependency;
    
    public FieldInjectionBean() {
        System.out.println("FieldInjectionBean constructor called");
    }
    
    public SimpleBean getDependency() {
        return dependency;
    }
    
    @Override
    public String toString() {
        return "FieldInjectionBean [dependency=" + dependency + "]";
    }
}
