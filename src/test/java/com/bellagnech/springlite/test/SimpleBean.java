package com.bellagnech.springlite.test;

public class SimpleBean {
    private String message = "Default Message";
    
    public SimpleBean() {
        System.out.println("SimpleBean constructor called");
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "SimpleBean [message=" + message + "]";
    }
}
