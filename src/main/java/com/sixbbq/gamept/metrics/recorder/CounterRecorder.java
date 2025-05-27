package com.sixbbq.gamept.metrics.recorder;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CounterRecorder {

    private final MeterRegistry registry;

    @Autowired
    public CounterRecorder(MeterRegistry registry) {
        this.registry = registry;
    }

    public void increment(String name, String... tags) {
        registry.counter(name, tags).increment();
    }
}