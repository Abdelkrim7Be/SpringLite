package com.bellagnech.springlite.test.advanced;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

@Component
public class ServiceC {
    
    private ServiceA serviceA;
    private ServiceB serviceB;
    
    public ServiceC() {
        System.out.println("ServiceC created with default constructor");
    }
    
    @Autowired
    public void setServiceA(ServiceA serviceA) {
        System.out.println("ServiceC.setServiceA called with " + serviceA.getName());
        this.serviceA = serviceA;
    }
    
    @Autowired
    public void setServiceB(ServiceB serviceB) {
        System.out.println("ServiceC.setServiceB called with " + serviceB.getMessage());
        this.serviceB = serviceB;
    }
    
    public String getCompleteMessage() {
        return "ServiceC using " + serviceA.getName() + " and " + serviceB.getMessage();
    }
}
