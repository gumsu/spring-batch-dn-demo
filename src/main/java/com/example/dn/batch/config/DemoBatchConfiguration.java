package com.example.dn.batch.config;

import com.example.dn.batch.domain.Restaurant;
import com.example.dn.batch.listener.BatchExecutionLoggerListener;
import com.example.dn.batch.CsvRangePartitioner;
import com.example.dn.batch.RestaurantItemReader;
import com.example.dn.batch.RestaurantItemWriter;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DemoBatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final @Lazy BatchExecutionLoggerListener batchExecutionLoggerListener;
    private final RestaurantItemReader restaurantItemReader;
    private final RestaurantItemWriter restaurantItemWriter;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job importJob() throws Exception {
        return new JobBuilder("importJob", jobRepository)
            .incrementer(new RunIdIncrementer()) /* 반복 테스트를 위해 */
            .start(masterStep())
            .listener(batchExecutionLoggerListener)
            .build();
    }

    @Bean
    public Step masterStep() throws Exception {
        return new StepBuilder("masterStep", jobRepository)
            .partitioner(slaveStep().getName(), new CsvRangePartitioner(1000, 100))
            .step(slaveStep())
            .gridSize(10)
            .taskExecutor(new SimpleAsyncTaskExecutor())
            .listener(batchExecutionLoggerListener)
            .build();
    }

    @Bean
    public Step slaveStep() throws Exception {
        return new StepBuilder("slaveStep", jobRepository)
            .<Restaurant, Restaurant>chunk(CHUNK_SIZE, platformTransactionManager)
            .reader(restaurantItemReader.reader(0, 0))
            .writer(restaurantItemWriter.writer())
            .listener(batchExecutionLoggerListener)
            .faultTolerant()
            .retryLimit(3)
            .retry(SQLException.class)
            .build();
    }
}
