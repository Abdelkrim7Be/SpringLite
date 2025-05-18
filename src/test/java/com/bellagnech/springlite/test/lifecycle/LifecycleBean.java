package com.bellagnech.springlite.test.lifecycle;

import com.bellagnech.springlite.di.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean that tracks its lifecycle events.
 */
@Component
public class LifecycleBean {
    
    private static final List<String> events = new ArrayList<>();
    
    public LifecycleBean() {
        addEvent("Constructor called");
    }
    
    public static void addEvent(String event) {
        events.add(event);
    }
    
    public static List<String> getEvents() {
        return new ArrayList<>(events);
    }
    
    public static void clearEvents() {
        events.clear();
    }
}
