package com.example.dn.batch;

import com.example.dn.batch.domain.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class DeleteTasklet implements Tasklet {

    private final RestaurantMapper mapper;

    @Value("#{jobParameters['baseDt']}")
    private String baseDt;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        int deleted = mapper.deleteByBaseDt(baseDt);
        return RepeatStatus.FINISHED;
    }
}
