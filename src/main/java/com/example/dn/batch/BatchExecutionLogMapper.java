package com.example.dn.batch;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BatchExecutionLogMapper {
    void insertJobLog(BatchExecutionLog log);
    void insertStepLog(BatchExecutionLog log);
    void updateJobLog(BatchExecutionLog log);
    void updateStepLog(BatchExecutionLog log);
}
