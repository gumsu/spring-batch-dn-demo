package com.example.dn.batch.config;

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
            .reader(reader(null,0, 0))
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
                        return null; // 더 이상 읽을 것이 없음
                    }

                    if (currentLine < startLine) {
                        continue; // 아직 시작 라인 도달 전
                    }

                    if (currentLine > endLine) {
                        return null; // 읽어야 할 범위 초과
                    }

                    item.setBaseDt(baseDt);
                    return item;
                }
            }
        };

        reader.setResource(new ClassPathResource("sample.csv"));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");

        /* defaultLineMapper: 읽으려는 데이터 LineMapper을 통해 Dto로 매핑 */
        DefaultLineMapper<Restaurant> defaultLineMapper = new DefaultLineMapper<>();

        /* delimitedLineTokenizer : txt 파일에서 구분자 지정하고 구분한 데이터 setNames를 통해 각 이름 설정 */
        DelimitedLineTokenizer delimitedLineTokenizer = new CustomDelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");

        delimitedLineTokenizer.setNames(
            "sequence", "openServiceName",	"openServiceId", "municipalityCode", "managementNumber", "permissionDate",
            "cancellationDate",	"businessStatusCode", "businessStatusName",	"detailedBusinessStatusCode", "detailedBusinessStatusName",
            "closureDate", "suspensionStartDate",	"suspensionEndDate", "reopenDate", "contactNumber", "siteArea", "postalCode",
            "fullAddress", "roadNameAddress", "roadNamePostalCode", "businessName", "lastModifiedDate", "dataUpdateType", "dataUpdateDate",
            "businessCategory", "coordinateX", "coordinateY", "sanitationType", "numberOfMaleEmployees", "numberOfFemaleEmployees",
            "businessAreaType", "grade", "waterSupplyType", "totalEmployees", "headOfficeEmployees"	, "factoryOfficeEmployees", "factorySalesEmployees",
            "factoryProductionEmployees", "buildingOwnershipType",	"securityDeposit", "monthlyRent", "isMultiUseFacility",	"totalFacilitySize",
            "traditionalBusinessDesignationNumber", "mainDishOfTraditionalBusiness", "homepage" );//행으로 읽은 데이터 매칭할 데이터 각 이름
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer); //lineTokenizer 설정

        /* beanWrapperFieldSetMapper: 매칭할 class 타입 지정 */
        BeanWrapperFieldSetMapper<Restaurant> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(Restaurant.class);
        beanWrapperFieldSetMapper.setStrict(false);

        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper); //fieldSetMapper 지정

        reader.setLineMapper(defaultLineMapper); //lineMapper 지정
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
