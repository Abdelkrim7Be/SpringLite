package com.bellagnech.springlite.test.advanced;

import com.bellagnech.springlite.di.annotations.Component;

@Component
public class ServiceA {
    
    private static int instanceCount = 0;
    private final int instanceId;
    
    public ServiceA() {
        instanceId = ++instanceCount;
        System.out.println("ServiceA #" + instanceId + " created");
    }
    
    public String getName() {
        return "ServiceA #" + instanceId;
    }
}
