package com.sixbbq.gamept.metrics.recorder;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GaugeRecorder {

    private final MeterRegistry registry;

    @Autowired
    public GaugeRecorder(MeterRegistry registry) {
        this.registry = registry;
    }

    public void registerListSize(String name, List<?> list) {
        registry.gauge(name, list, List::size);
    }
}