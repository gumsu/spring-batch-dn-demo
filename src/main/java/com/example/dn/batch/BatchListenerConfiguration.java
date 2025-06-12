package com.example.dn.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchListenerConfiguration {

    @Bean
    public BatchExecutionLoggerListener batchExecutionLoggerListener(BatchExecutionLogMapper mapper) {
        return new BatchExecutionLoggerListener(mapper);
    }
}
