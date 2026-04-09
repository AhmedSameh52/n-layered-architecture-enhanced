package com.example.nlayered.common.config;

import lombok.extern.slf4j.Slf4j;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JooqConfig {

    /**
     * Logs queries that exceed 500 ms at WARN level.
     * Uses a ThreadLocal to track start time across executeStart/executeEnd lifecycle events.
     */
    @Bean
    public ExecuteListenerProvider slowQueryListener() {
        return new DefaultExecuteListenerProvider(new SlowQueryListener());
    }

    private static class SlowQueryListener implements ExecuteListener {

        private final ThreadLocal<Long> startTime = new ThreadLocal<>();

        @Override
        public void executeStart(ExecuteContext ctx) {
            startTime.set(System.currentTimeMillis());
        }

        @Override
        public void executeEnd(ExecuteContext ctx) {
            Long start = startTime.get();
            if (start == null) return;
            startTime.remove();

            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > 500 && ctx.query() != null) {
                log.warn("Slow jOOQ query ({} ms): {}", elapsed, ctx.query().getSQL());
            }
        }
    }
}