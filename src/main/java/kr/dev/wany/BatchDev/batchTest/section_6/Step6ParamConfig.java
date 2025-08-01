package kr.dev.wany.BatchDev.batchTest.section_6;

import lombok.RequiredArgsConstructor;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/************************************
 * Name : Step6ParamConfig
 * To-Do : 기본 데이터 타입 파라미터 사용
 * Developer : twkim
 * Date : 2025-07-30 오후 2:37
 ************************************/
@Configuration
@RequiredArgsConstructor
public class Step6ParamConfig
{
    private static final Logger logloc = LoggerFactory.getLogger( Step6ParamConfig.class );

    @Bean
    public Job processSection6(JobRepository jobRepository, Step terminationStep) {
        return new JobBuilder( "processSection6", jobRepository)
                .start( terminationStep )
                .build();
    }

    @Bean
    public Step terminationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet terminationTasklet) {
        return new StepBuilder( "terminationStep", jobRepository )
                .tasklet( terminationTasklet, transactionManager )
                .build();
    }


    /**
     * @StepScope : @Value 를 사용해 Job parameter를 전달받기 위해 선언
     * @param terminatorId
     * @param targetCount
     * @return
     */
    @Bean
    @StepScope
    public Tasklet terminationTasklet(
            @Value( "#{jobParameters['terminatorId']}" ) String terminatorId,
            @Value( "#{jobParameters['targetCount']}") Integer targetCount
    ) {
        return (contribution, chunkContext) -> {
            logloc.info( "시스템 종결자 정보:" );
            logloc.info( "ID: {}", terminatorId );
            logloc.info( "제거 대상 수: {}", targetCount );
            logloc.info( "SYSTEM TERMINATOR {} 작전 개시합니다.", terminatorId );
            logloc.info( "{} 개의 프로세스를 종료합니다.", targetCount );

            for( int idx = 1; idx <= targetCount; idx++ ) {
                logloc.info( "\uD83D\uDC80 프로세스 {} 종료 완료.", idx );
            }

            logloc.info( "임무 완료: 모둔 대상 프로세스가 종료되었습니다." );
            return RepeatStatus.FINISHED;
        };
    }
}
