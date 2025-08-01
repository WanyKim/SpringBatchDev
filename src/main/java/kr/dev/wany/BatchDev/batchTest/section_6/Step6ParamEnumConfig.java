package kr.dev.wany.BatchDev.batchTest.section_6;

import kr.dev.wany.BatchDev.batchTest.section_6.info.QuestDifficulty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Locale;

/************************************
 * Name : Step6ParamEnumConfig
 * To-Do : Enum 타입의 Job 파라미터를 전달하는 예제
 * Developer : twkim
 * Date : 2025-08-01 오후 3:17
 ************************************/
@Configuration
public class Step6ParamEnumConfig {

    private static final Logger logloc = LoggerFactory.getLogger( Step6ParamEnumConfig.class );

    @Bean
    public Job step6ParamEnumJob( JobRepository jobRepository, Step step6ParamEnumStep ) {
        return new JobBuilder( "step6ParamEnumJob", jobRepository )
                .start( step6ParamEnumStep )
                .build();
    }


    @Bean
    public Step step6ParamEnumStep( JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet step6ParamEnumTasklet ) {
        return new StepBuilder( "step6ParamEnumStep", jobRepository )
                .tasklet( step6ParamEnumTasklet, new ResourcelessTransactionManager() )
                .build();
    }


    @Bean
    @StepScope
    public Tasklet step6ParamEnumTasklet(
        @Value( "#{jobParameters['questDifficulty']}" ) QuestDifficulty questDifficulty
    ) {
        return (contribution, chunkContext) -> {
            logloc.info("⚔️ 시스템 침투 작전 개시!");
            logloc.info("임무 난이도: {}", questDifficulty);
            
            // 난이도에 따른 보상 계산
            int baseReward = 100;
            int rewardMultiplier = switch (questDifficulty)
            {
                case EASY -> 1;
                case MEDIUM -> 2;
                case HARD -> 3;
                case EXTREME -> 4;
            };

            int totalReward = baseReward + rewardMultiplier;

            logloc.info("💥 시스템 해킹 진행 중...");
            logloc.info("🏆 시스템 장악 완료!");
            logloc.info("💰 획득한 시스템 리소스: {} 메가바이트", totalReward);

            return RepeatStatus.FINISHED;
        };
    }
}
