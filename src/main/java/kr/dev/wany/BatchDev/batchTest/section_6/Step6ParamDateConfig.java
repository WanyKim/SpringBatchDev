package kr.dev.wany.BatchDev.batchTest.section_6;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/************************************
 * Name : Step6ParamDateConfig
 * To-Do : 날짜 타입을 Parameter 로 받아서 실행 Job
 * - executionDate는 'yyyy-MM-dd' 형식을, startTime은 'yyyy-MM-ddThh:mm:ss' 형식을 사용
 * - DateTimeFormatter의 ISO_LOCAL_DATE와 ISO_LOCAL_DATE_TIME 형식과 일치한다. 날짜/시간 타입의 잡 파라미터는 이러한 ISO 표준 형식
 * Developer : twkim
 * Date : 2025-07-30 오후 5:59
 ************************************/
@Configuration
public class Step6ParamDateConfig {

    private static final Logger logloc = LoggerFactory.getLogger( Step6ParamDateConfig.class );

    @Bean
    public Job Step6ParamDateJob(JobRepository jobRepository , Step terminateStep) {
        return new JobBuilder( "Step6ParamDateJob", jobRepository )
                .start( terminateStep )
                .build();
    }


    @Bean
    public Step terminateStep( JobRepository jobRepository , PlatformTransactionManager transactionManager, Tasklet terminateTasklet) {
        return new StepBuilder( "terminateStep", jobRepository )
                .tasklet( terminateTasklet, transactionManager )
                .build();
    }


    @Bean
    @StepScope
    public Tasklet terminateTasklet(
            @Value("#{JobParameters['executionDate']}") LocalDate executionDate,
            @Value("#{JobParameters['startTime']}") LocalDateTime startTime
            ) {
        return (contribution, chunkContext) -> {
            logloc.info("시스템 처형 정보:");
            logloc.info("처형 예정일: {}", executionDate.format( DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            logloc.info("작전 개시 시각: {}", startTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));
            logloc.info("⚡ {}에 예정된 시스템 정리 작전을 개시합니다.", executionDate);
            logloc.info("💀 작전 시작 시각: {}", startTime);

            // 작전 진행 상황 추적
            LocalDateTime currentTime = startTime;
            for (int i = 1; i <= 3; i++) {
                currentTime = currentTime.plusHours(1);
                logloc.info("☠️ 시스템 정리 {}시간 경과... 현재 시각:{}", i, currentTime.format(DateTimeFormatter.ofPattern("HH시 mm분")));
            }

            logloc.info("🎯 임무 완료: 모든 대상 시스템이 성공적으로 제거되었습니다.");
            logloc.info("⚡ 작전 종료 시각: {}", currentTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));


            return RepeatStatus.FINISHED;
        };
    }
}
