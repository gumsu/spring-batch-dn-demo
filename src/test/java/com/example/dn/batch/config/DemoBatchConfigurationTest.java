package com.example.dn.batch.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBatchTest
@SpringBootTest
@TestPropertySource(properties = {
    "spring.batch.job.enabled=false"
})
class DemoBatchConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private DemoBatchConfiguration demoBatchConfiguration;

    @Test
    void importJob_정상수행() throws Exception {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("baseDt", "20250612")
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters();

        // When
        var jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertNotNull(jobExecution);
        assertTrue(jobExecution.getExitStatus().getExitCode().equals("COMPLETED"));
    }

    @Test
    void deleteStep_정상수행() {
        // Given
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("baseDt", "20250612")
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters();

        // When
        var jobExecution = jobLauncherTestUtils.launchStep("deleteStep", jobParameters);

        // Then
        assertNotNull(jobExecution);
        assertTrue(jobExecution.getExitStatus().getExitCode().equals("COMPLETED"));
    }

}
