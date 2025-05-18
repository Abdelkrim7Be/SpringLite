package com.bellagnech.springlite.test.annotation;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

@Component
public class SetterInjectionBean {
    
    private SimpleService simpleService;
    
    public SetterInjectionBean() {
        System.out.println("SetterInjectionBean created with default constructor");
    }
    
    @Autowired
    public void setSimpleService(SimpleService simpleService) {
        System.out.println("SetterInjectionBean.setSimpleService called");
        this.simpleService = simpleService;
    }
    
    public String getServiceMessage() {
        return "SetterInjectionBean says: " + simpleService.getMessage();
    }
}
