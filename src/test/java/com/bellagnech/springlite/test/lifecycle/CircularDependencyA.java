package com.bellagnech.springlite.test.lifecycle;

import com.bellagnech.springlite.di.annotations.Autowired;
import com.bellagnech.springlite.di.annotations.Component;

/**
 * Bean with a circular dependency to demonstrate error detection.
 */
@Component
public class CircularDependencyA {
    
    @Autowired
    private CircularDependencyB circularDependencyB;
    
    public CircularDependencyA() {
        LifecycleBean.addEvent("CircularDependencyA constructor called");
    }
    
    public CircularDependencyB getCircularDependencyB() {
        return circularDependencyB;
    }
}
