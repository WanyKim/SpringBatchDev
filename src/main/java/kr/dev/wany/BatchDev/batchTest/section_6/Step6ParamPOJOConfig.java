package kr.dev.wany.BatchDev.batchTest.section_6;

import kr.dev.wany.BatchDev.batchTest.section_6.info.Step6ParamPOJO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/************************************
 * Name : step6ParamPOJOConfig
 * To-Do : 
 * Developer : twkim
 * Date : 2025-08-01 오후 5:24
 ************************************/
@Configuration
public class Step6ParamPOJOConfig
{
    private static final Logger logloc = LoggerFactory.getLogger( Step6ParamPOJOConfig.class );

    @Bean
    public Job step6ParamPOJOJob(JobRepository jobRepository, Step step6ParamPOJOStep )
    {
        return new JobBuilder( "step6ParamPOJOJob", jobRepository )
                .start( step6ParamPOJOStep )
                .build();
    }


    @Bean
    public Step step6ParamPOJOStep(JobRepository jobRepository, Tasklet step6ParamPOJOTasklet) {
        return new StepBuilder( "step6ParamPOJOStep", jobRepository )
                .tasklet( step6ParamPOJOTasklet, new ResourcelessTransactionManager() )
                .build();
    }

    @Bean
    public Tasklet step6ParamPOJOTasklet(Step6ParamPOJO step6ParamPOJO) {
        return (contribution, chunkContext) -> {
            logloc.info("⚔️ 시스템 침투 작전 초기화!");
            logloc.info("임무 코드네임: {}", step6ParamPOJO.getMissionName());
            logloc.info("보안 레벨: {}", step6ParamPOJO.getSecurityLevel());
            logloc.info("작전 지휘관: {}", step6ParamPOJO.getOperationCommander());

            // 보안 레벨에 따른 침투 난이도 계산
            int baseInfiltrationTime = 60; // 기본 침투 시간 (분)
            int infiltrationMultiplier = switch (step6ParamPOJO.getSecurityLevel()) {
                case 1 -> 1; // 저보안
                case 2 -> 2; // 중보안
                case 3 -> 4; // 고보안
                case 4 -> 8; // 최고 보안
                default -> 1;
            };

            int totalInfiltrationTime = baseInfiltrationTime * infiltrationMultiplier;

            logloc.info("💥 시스템 해킹 난이도 분석 중...");
            logloc.info("🕒 예상 침투 시간: {}분", totalInfiltrationTime);
            logloc.info("🏆 시스템 장악 준비 완료!");

            return RepeatStatus.FINISHED;
        };
    }
}
