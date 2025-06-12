package com.example.dn.batch;

import com.example.dn.batch.domain.Restaurant;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class RestaurantItemReader {

    @Bean
    @StepScope
    public FlatFileItemReader<Restaurant> reader(
        @Value("#{stepExecutionContext['startLine']}") int startLine,
        @Value("#{stepExecutionContext['endLine']}") int endLine) {
        FlatFileItemReader<Restaurant> reader = new FlatFileItemReader<>() {
            private int currentLine = 0;

            @Override
            public Restaurant read() throws Exception {
                Restaurant item;
                do {
                    item = super.read();
                    currentLine ++;
                } while (item != null && (currentLine < startLine || currentLine > endLine));

                return (currentLine > endLine) ? null : item;
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

    static class CustomDelimitedLineTokenizer extends DelimitedLineTokenizer {

        private static final char DEFAULT_QUOTE_CHARACTER = '"';

        @Override
        protected boolean isQuoteCharacter(char c) {
            return c == DEFAULT_QUOTE_CHARACTER;
        }
    }
}
