package com.bellagnech.springlite.di;

/**
 * A bean definition describes the metadata of a bean.
 * It contains details like the bean class name, id, and scope.
 */
public class BeanDefinition {
    
    private String id;
    private String className;
    private String scope = "singleton"; // Default scope
    
    public BeanDefinition() {
    }
    
    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }
    
    public BeanDefinition(String id, String className, String scope) {
        this.id = id;
        this.className = className;
        this.scope = scope;
    }
    
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
    
    @Override
    public String toString() {
        return "BeanDefinition{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
