package com.bellagnech.springlite.test.lifecycle;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

/**
 * Bean with a circular dependency to demonstrate error detection.
 */
@Component
public class CircularDependencyB {
    
    @Autowired
    private CircularDependencyA circularDependencyA;
    
    public CircularDependencyB() {
        LifecycleBean.addEvent("CircularDependencyB constructor called");
    }
    
    public CircularDependencyA getCircularDependencyA() {
        return circularDependencyA;
    }
}
