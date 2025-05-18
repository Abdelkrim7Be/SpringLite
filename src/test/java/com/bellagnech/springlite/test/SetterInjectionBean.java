package com.bellagnech.springlite.test;

public class SetterInjectionBean {
    private SimpleBean dependency;
    
    public SetterInjectionBean() {
        System.out.println("SetterInjectionBean constructor called");
    }
    
    public SimpleBean getDependency() {
        return dependency;
    }
    
    public void setDependency(SimpleBean dependency) {
        System.out.println("Setter injection called with " + dependency);
        this.dependency = dependency;
    }
    
    @Override
    public String toString() {
        return "SetterInjectionBean [dependency=" + dependency + "]";
    }
}
