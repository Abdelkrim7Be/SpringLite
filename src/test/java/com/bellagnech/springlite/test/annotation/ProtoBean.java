package com.bellagnech.springlite.test.annotation;

import com.bellagnech.springlite.di.annotations.Component;
import com.bellagnech.springlite.di.annotations.Scope;

@Component
@Scope("prototype")
public class ProtoBean {
    
    private static int instanceCount = 0;
    private final int instanceNumber;
    
    public ProtoBean() {
        instanceNumber = ++instanceCount;
        System.out.println("ProtoBean created, instance #" + instanceNumber);
    }
    
    public int getInstanceNumber() {
        return instanceNumber;
    }
}
