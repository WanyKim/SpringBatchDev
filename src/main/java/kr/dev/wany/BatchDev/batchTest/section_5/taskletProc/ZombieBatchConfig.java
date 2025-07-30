package kr.dev.wany.BatchDev.batchTest.section_5.taskletProc;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/************************************
 * Name : ZombieBatchConfig
 * To-Do : 
 * Developer : twkim
 * Date : 2025-07-30 오전 11:10
 ************************************/
@Configuration
public class ZombieBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ZombieBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job zombieCleanJob() {
        return new JobBuilder( "zombieCleanJob", jobRepository )
                .start( zombieCleanupStep() )
                .build();
    }

    @Bean
    public Tasklet zombieProcessCleanupTasklet() {
        return new ZombieProcessCleanupTasklet();
    }

    @Bean
    public Step zombieCleanupStep() {
        return new StepBuilder( "zombieCleanupStep", jobRepository )
                // DB Transaction 을 고려할 필요가 없기에 transactionManager 을 사용하지 않는다. (transactionManager를 사용해도 무관하나, 불필요한 DB 연결울 수행한다.)
//                .tasklet( zombieProcessCleanupTasklet(), transactionManager )
                .tasklet( zombieProcessCleanupTasklet(), new ResourcelessTransactionManager() )
                .build();
    }

}
