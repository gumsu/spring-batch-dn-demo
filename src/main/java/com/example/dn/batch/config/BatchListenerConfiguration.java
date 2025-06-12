package com.example.dn.batch.config;

import com.example.dn.batch.listener.BatchExecutionLogMapper;
import com.example.dn.batch.listener.BatchExecutionLoggerListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchListenerConfiguration {

    @Bean
    public BatchExecutionLoggerListener batchExecutionLoggerListener(BatchExecutionLogMapper mapper) {
        return new BatchExecutionLoggerListener(mapper);
    }
}
