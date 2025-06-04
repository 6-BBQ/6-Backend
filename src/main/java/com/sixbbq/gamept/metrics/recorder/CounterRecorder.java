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
        System.out.println("🔧 CounterRecorder.increment 호출됨");
        System.out.println("🔧 name: " + name);
        System.out.println("🔧 tags: " + java.util.Arrays.toString(tags));
        
        try {
            registry.counter(name, tags).increment();
            System.out.println("🔧 registry.counter().increment() 성공");
            
            // 현재 값 확인
            double currentValue = registry.counter(name, tags).count();
            System.out.println("🔧 현재 카운터 값: " + currentValue);
            
            // 레지스트리에서 signup 관련 메터 확인
            System.out.println("🔧 레지스트리에 등록된 signup 메터:");
            registry.getMeters().forEach(meter -> {
                if (meter.getId().getName().contains("signup")) {
                    System.out.println("🔧   - " + meter.getId().getName() + " " + meter.getId().getTags() + " = " + meter.measure());
                }
            });
            
        } catch (Exception e) {
            System.out.println("🔧 registry.counter().increment() 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}