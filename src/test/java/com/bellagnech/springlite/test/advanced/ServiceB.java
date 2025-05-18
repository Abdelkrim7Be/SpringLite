package com.bellagnech.springlite.test.advanced;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

@Component
public class ServiceB {
    
    private final ServiceA serviceA;
    
    @Autowired
    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
        System.out.println("ServiceB created with " + serviceA.getName());
    }
    
    public String getMessage() {
        return "ServiceB using " + serviceA.getName();
    }
}
