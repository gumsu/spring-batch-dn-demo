package com.example.dn.batch.config;

import com.example.dn.batch.DeleteTasklet;
import com.example.dn.batch.domain.Restaurant;
import com.example.dn.batch.listener.BatchExecutionLoggerListener;
import com.example.dn.batch.CsvRangePartitioner;
import com.example.dn.batch.RestaurantItemWriter;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DemoBatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final @Lazy BatchExecutionLoggerListener batchExecutionLoggerListener;
    private final DeleteTasklet deleteTasklet;
    private final RestaurantItemWriter restaurantItemWriter;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job importJob() throws Exception {
        return new JobBuilder("importJob", jobRepository)
            .incrementer(new RunIdIncrementer()) /* 반복 테스트를 위해 */
            .start(deleteStep())
            .next(masterStep())
            .listener(batchExecutionLoggerListener)
            .build();
    }

    @Bean
    public Step deleteStep() {
        return new StepBuilder("deleteStep", jobRepository)
            .tasklet(deleteTasklet, platformTransactionManager)
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
            .reader(reader(null, 0, 0))
            .writer(restaurantItemWriter.writer())
            .listener(batchExecutionLoggerListener)
            .faultTolerant()
            .retryLimit(3)
            .retry(SQLException.class)
            .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Restaurant> reader(
        @Value("#{jobParameters['baseDt']}") String baseDt,
        @Value("#{stepExecutionContext['startLine']}") int startLine,
        @Value("#{stepExecutionContext['endLine']}") int endLine) {
        FlatFileItemReader<Restaurant> reader = new FlatFileItemReader<>() {
            private int currentLine = 0;

            @Override
            public Restaurant read() throws Exception {
                while (true) {
                    Restaurant item = super.read();
                    currentLine++;

                    if (item == null) {
                        return null;
                    }

                    if (currentLine < startLine) {
                        continue;
                    }

                    if (currentLine > endLine) {
                        return null;
                    }

                    item.setBaseDt(baseDt);
                    return item;
                }
            }
        };

        reader.setResource(new ClassPathResource("sample2.csv"));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");

        DefaultLineMapper<Restaurant> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new CustomDelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");

        delimitedLineTokenizer.setNames(
            "sequence", "openServiceName", "openServiceId", "municipalityCode", "managementNumber",
            "permissionDate",
            "cancellationDate", "businessStatusCode", "businessStatusName",
            "detailedBusinessStatusCode", "detailedBusinessStatusName",
            "closureDate", "suspensionStartDate", "suspensionEndDate", "reopenDate",
            "contactNumber", "siteArea", "postalCode",
            "fullAddress", "roadNameAddress", "roadNamePostalCode", "businessName",
            "lastModifiedDate", "dataUpdateType", "dataUpdateDate",
            "businessCategory", "coordinateX", "coordinateY", "sanitationType",
            "numberOfMaleEmployees", "numberOfFemaleEmployees",
            "businessAreaType", "grade", "waterSupplyType", "totalEmployees", "headOfficeEmployees",
            "factoryOfficeEmployees", "factorySalesEmployees",
            "factoryProductionEmployees", "buildingOwnershipType", "securityDeposit", "monthlyRent",
            "isMultiUseFacility", "totalFacilitySize",
            "traditionalBusinessDesignationNumber", "mainDishOfTraditionalBusiness", "homepage");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        BeanWrapperFieldSetMapper<Restaurant> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(Restaurant.class);
        beanWrapperFieldSetMapper.setStrict(false);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

        reader.setLineMapper(defaultLineMapper);
        return reader;
    }

    // 따옴표 안에 콤마는 무시
    static class CustomDelimitedLineTokenizer extends DelimitedLineTokenizer {

        private static final char DEFAULT_QUOTE_CHARACTER = '"';

        @Override
        protected boolean isQuoteCharacter(char c) {
            return c == DEFAULT_QUOTE_CHARACTER;
        }
    }
}
