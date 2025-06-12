package com.example.dn.batch.listener;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BatchExecutionLog {
    private Long id;
    private String jobName;
    private Long jobExecutionId;
    private String stepName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String exitCode;
    private String exitMessage;
    private Long readCount;
    private Long writeCount;
    private Long commitCount;
    private Long rollbackCount;
    private String errorMessage;
    private LocalDateTime createdAt;
}
