package kr.dev.wany.BatchDev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication
public class BatchDevApplication {

    private static final Logger logloc = LoggerFactory.getLogger( BatchDevApplication.class );
    
    /*********************************
     * [ TIPs.]
     *
     * ApplicationContext에 등록된 모든 Job 타입의 bean을 찾아 Map 형태로 변환.
     * Key : Job bean 의 이름
     * [ Tips. ]
     * ApplicationContext 는 container 이며, application 시작 시 개발자가 정의한 모든
     * @Component, @Service, @Repository, @Controller, @Configuration 클래스 및 @Bean 메서드를 스캔하고,
     * 이들을 생성하는 객체들을 singleton 으로 관리하며 bean 으로 등록한다.
     *
     * [ Bean 정의 & Scan & regist ]
     * 1. application이 시작될 때 Spring 은 BatchDevApplication 의 @ComponentScan 이 지정한 패키지를 스캐.
     * 2. 이 패키지 안에 있는 @Configuration 클래스(Ex: SystemTerminationConfig.java) 를 찾아 @Bean 이 붙어 있는 메서드를 분석.
     * 3. 각 @Bean 메서드가 반환하는 Job 객체들을 생성 후 Spring Context 내부에 bean 으로 등록.
     *    이때 bean 의 이름은 @Bean 에 지정된 이름 (@Bean("test") 의 "test") 가 되고, 타입은 Job 인터페이스를 구현한 클래스가 된다.
     *    만약 @Bean 에 이름을 지정하지 않으면, method 명이 Bean 의 이름이 된다.
     *********************************/
    public static void main(String[] args) {
        // 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달하기 위함.
        System.exit( SpringApplication.exit( SpringApplication.run(BatchDevApplication.class, args) ) );
    }

    @Bean
    public CommandLineRunner customJobRunner(JobLauncher jobLauncher, ApplicationContext applicationContext) {
        return args -> {
            // 1. 실행할 Job 이름 목록 Parsing
            List<String> jobNames = Arrays.stream(args)
                    .filter( arg -> arg.startsWith( "--spring.batch.job.names=" ) )
                    .map( arg -> arg.substring( "--spring.batch.job.names=".length() ) )
                    .flatMap( names -> Arrays.stream( names.split( "," ) ) )
                    .filter( StringUtils :: hasText )
                    .toList();

            if ( jobNames.isEmpty() ) {
                logloc.error( "[ERROR] No Job names provided to run with --spring.batch.job.names." );
                return;
            }

            // 2. Job parameter parsing (parameter에 타입값 (Integer) 이 붙어있을 경우 대비
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

            Arrays.stream(args)
                    .filter(arg -> !arg.startsWith("--spring.batch.job.names="))
                    .forEach(arg -> {
                        String[] parts = StringUtils.split(arg, "=");
                        if (parts != null && parts.length == 2) {
                            String key = parts[0];
                            String rawValue = parts[1]; // 예: "HARD(package...)"

                            // 괄호'('가 있으면 그 앞부분만 값으로 사용
                            int parenthesisIndex = rawValue.indexOf('(');
                            String finalValue = (parenthesisIndex != -1) ? rawValue.substring(0, parenthesisIndex) : rawValue;

                            boolean identifying = !key.startsWith("-");
                            if (!identifying) {
                                key = key.substring(1);
                            }

                            jobParametersBuilder.addString(key, finalValue, identifying);
                        }
                    });
//
//            Properties jobProperties = new Properties();
//            Arrays.stream( args )
//                    .filter( arg -> !arg.startsWith( "--spring.batch.job.names=" ) )
//                    .forEach( arg -> {
//                        String[] parts = StringUtils.split( arg, "=" );
//
//                        if( parts != null && parts.length == 2 ) {
//                            jobProperties.setProperty( parts[0], parts[1] );
//                        }
//                    } );

            // 3. 공통 JobParameter 생성.
            JobParameters baseParameters = jobParametersBuilder.toJobParameters();

            logloc.info( "Found {} jobs to execute: {}", jobNames.size(), jobNames );

            // 4. 지정된 모든 Job 을 순차적으로 실행.
            for( String jobName : jobNames) {
                try
                {
                    Job jobToRun = applicationContext.getBean(jobName, Job.class);

                    // 매 실행마다 고유한 JobInstance를 갖도록 현재 시간을 파라미터에 추가
                    JobParameters finalParameters = new JobParametersBuilder( baseParameters)
                            .addDate( "run.timestamp", new Date() )
                            .toJobParameters();

                    logloc.info( "Executing job '{}' with parameters : {}", jobName, finalParameters );
                    jobLauncher.run(jobToRun, finalParameters);
                }
                catch ( Exception e )
                {
                    logloc.error( "[ERROR] Failed to execute job '{}'. E: {}", jobName, e.getMessage() );
                }
            }
        };
    }
}
