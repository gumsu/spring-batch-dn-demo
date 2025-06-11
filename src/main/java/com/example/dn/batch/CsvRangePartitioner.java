package com.example.dn.batch;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class CsvRangePartitioner implements Partitioner {

    private final int totalLines;
    private final int linesPerPartition;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        int start = 1;

        for (int i = 0; i < gridSize; i++) {
            int end = Math.min(start + linesPerPartition - 1, totalLines);
            ExecutionContext context = new ExecutionContext();
            context.putInt("startLine", start);
            context.putInt("endLine", end);
            partitions.put("partition" + i, context);
            start = end + 1;
        }
        return partitions;
    }
}
