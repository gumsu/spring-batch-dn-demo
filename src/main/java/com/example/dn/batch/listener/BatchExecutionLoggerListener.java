package com.example.dn.batch.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@RequiredArgsConstructor
public class BatchExecutionLoggerListener implements JobExecutionListener, StepExecutionListener {

    private final BatchExecutionLogMapper mapper;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        BatchExecutionLog log = BatchExecutionLog.builder()
            .jobName(jobExecution.getJobInstance().getJobName())
            .jobExecutionId(jobExecution.getId())
            .status(jobExecution.getStatus().toString())
            .build();
        mapper.insertJobLog(log);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchExecutionLog log = BatchExecutionLog.builder()
            .jobName(jobExecution.getJobInstance().getJobName())
            .jobExecutionId(jobExecution.getId())
            .status(jobExecution.getStatus().toString())
            .endTime(jobExecution.getEndTime())
            .exitCode(jobExecution.getExitStatus().getExitCode())
            .exitMessage(jobExecution.getExitStatus().getExitDescription())
            .build();

        if (!jobExecution.getFailureExceptions().isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (Throwable throwable : jobExecution.getFailureExceptions()) {
                errorMessage.append(throwable.getMessage());
            }
            log.setErrorMessage(errorMessage.toString());
        }
        mapper.updateJobLog(log);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        BatchExecutionLog log = BatchExecutionLog.builder()
            .jobName(stepExecution.getJobExecution().getJobInstance().getJobName())
            .jobExecutionId(stepExecution.getJobExecutionId())
            .stepName(stepExecution.getStepName())
            .status(stepExecution.getStatus().toString())
            .startTime(stepExecution.getStartTime())
            .build();
        mapper.insertStepLog(log);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        BatchExecutionLog log = BatchExecutionLog.builder()
            .jobName(stepExecution.getJobExecution().getJobInstance().getJobName())
            .stepName(stepExecution.getStepName())
            .jobExecutionId(stepExecution.getJobExecutionId())
            .status(stepExecution.getStatus().toString())
            .endTime(stepExecution.getEndTime())
            .exitCode(stepExecution.getExitStatus().getExitCode())
            .exitMessage(stepExecution.getExitStatus().getExitDescription())
            .readCount(stepExecution.getReadCount())
            .writeCount(stepExecution.getWriteCount())
            .commitCount(stepExecution.getCommitCount())
            .rollbackCount(stepExecution.getRollbackCount())
            .build();
        mapper.updateStepLog(log);
        return stepExecution.getExitStatus();
    }
}
