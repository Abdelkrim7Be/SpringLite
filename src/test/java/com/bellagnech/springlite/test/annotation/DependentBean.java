package com.bellagnech.springlite.test.annotation;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

@Component
public class DependentBean {
    
    private final SimpleService simpleService;
    
    @Autowired
    public DependentBean(SimpleService simpleService) {
        this.simpleService = simpleService;
        System.out.println("DependentBean created with constructor injection");
    }
    
    public String getServiceMessage() {
        return "DependentBean says: " + simpleService.getMessage();
    }
}
