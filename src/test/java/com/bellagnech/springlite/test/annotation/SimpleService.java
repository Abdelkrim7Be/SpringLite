package com.bellagnech.springlite.test.annotation;

import com.bellagnech.springlite.di.annotations.Component;

@Component
public class SimpleService {
    
    public String getMessage() {
        return "Hello from SimpleService";
    }
}
