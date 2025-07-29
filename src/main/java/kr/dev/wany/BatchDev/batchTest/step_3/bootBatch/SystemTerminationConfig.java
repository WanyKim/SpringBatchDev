package kr.dev.wany.BatchDev.batchTest.step_3.bootBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

/************************************
 * Name : SystemTerminationConfig
 * To-Do :
 * [ SpringBoot Batch 를 이용하기에 primitiveBatch 보다 간편해짐 ]
 * - @EnableBatchProcessing "
 * 1. enterWorldStep
 * 2. meetNPCStep
 * 3. defeatProcessStep
 * 4. completeQuestStep
 * Developer : twkim
 * Date : 2025-07-29 오후 1:57
 ************************************/
@Configuration
public class SystemTerminationConfig
{
    private static final Logger logloc = LoggerFactory.getLogger( SystemTerminationConfig.class );
    
    private AtomicInteger processKilled = new AtomicInteger(0);
    private final int TERMINATION_TARGET = 5;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;;

    public SystemTerminationConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean("systemTerminationSimulationJob")
    public Job systemTerminationSimulationJob() {

        /*********************************
         * 정의된 Step 들은 이전 Step이 성공적으로 완료된 이후 실행됨.
         * 즉, enterWorldStep 이 완료되어야 meetNPCStep 이 실행됨.
         * 만약 이전 Step이 실패하면 다음 Step은 실행되지 않음.
         *********************************/
        return new JobBuilder( "systemTerminationSimulationJob", jobRepository )
                .start( enterWorldStep() )
                .next( meetNPCStep() )
                .next( defeatProcessStep() )
                .next( completeQuestStep() )
                .build();
    }


    @Bean
    public Step enterWorldStep() {
        /*********************************
         * StepBuilder로 Step을 생성.
         * 첫번째 파라미터로 Step 식별자를 지정하며, Job 과 Step의 상태를 추적 및 제어할 때 사용됨.
         *
         * Step의 실제 동작은 tasklet() 메서드를 통해서 정의됨.
         * BatchConfig 으로 부터 주입받은 'PlatformTransactionManager' 가 이 지점에서 사용된다.
         *********************************/

        return new StepBuilder( "enterWorldStep", jobRepository )
                .tasklet( (contribution, chunkContext) -> {
                    System.out.println( "Access System Termination" );
                    return RepeatStatus.FINISHED;
                }, transactionManager )
                .build();
    }


    @Bean
    public Step meetNPCStep() {
        return new StepBuilder( "meetNPCStep", jobRepository )
                .tasklet( (contribution, chunkContext) -> {
                    System.out.println( "Meet NPC ( Administrator of System )" );
                    System.out.println( "First mission: kill " + TERMINATION_TARGET + " zombie!" );
                    return RepeatStatus.FINISHED;
                }, transactionManager )
                .build();
    }

    @Bean
    public Step defeatProcessStep() {
        return new StepBuilder( "defeatProcessStep", jobRepository )
                .tasklet( (contribution, chunkContext) -> {
                    int terminated = processKilled.incrementAndGet();

                    System.out.println( "Complete kill zombies (Curr " + terminated + "/" + TERMINATION_TARGET );

                    if( terminated < TERMINATION_TARGET ) {
                        return RepeatStatus.CONTINUABLE;
                    }
                    else {
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager )
                .build();
    }


    @Bean
    public Step completeQuestStep() {
        return new StepBuilder( "completeQuestStep", jobRepository )
                .tasklet( (contribution, chunkContext) -> {
                    System.out.println( "Mission complete! kill zombie " + TERMINATION_TARGET );
                    System.out.println( "Reward: kill -9 권한 획득." );
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
