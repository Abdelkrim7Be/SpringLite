package com.bellagnech.springlite.test.annotation;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

@Component
public class FieldInjectionBean {
    
    @Autowired
    private SimpleService simpleService;
    
    public FieldInjectionBean() {
        System.out.println("FieldInjectionBean created with default constructor");
    }
    
    public String getServiceMessage() {
        return "FieldInjectionBean says: " + simpleService.getMessage();
    }
}
