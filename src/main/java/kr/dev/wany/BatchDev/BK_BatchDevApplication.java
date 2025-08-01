package kr.dev.wany.BatchDev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


/************************************
 * Name : BK_BatchDevApplication
 * To-Do :
 * - 해당 방식은 JobParameter 를 표준 방식이 아닌 Custom 방식을 사용하였기에 CommandLineRunner 을 implement 하였고,
 *   argument 역시 직접 parsing 하여 사용한 방법이다.
 *   이전 Arguemnt : --spring.batch.job.names=processSection6 terminatorId=KILL-9,java.lang.String targetCount=5,java.lang.Integer,false
 *   'BatchDevApplication' 에서 SpringBoot 에서 제공되는 자동변환 방식을 사용하겠다.
 * Developer : twkim
 * Date : 2025-07-30 오후 2:37
 ************************************/
//@SpringBootApplication
//@EnableBatchProcessing  // Spring Batch 인프라 활성화
//public class BK_BatchDevApplication {
public class BK_BatchDevApplication implements CommandLineRunner {

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

    private static final Logger log = LoggerFactory.getLogger( BK_BatchDevApplication.class );

    public static void main(String[] args) {
        // 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달하기 위함.
        System.exit( SpringApplication.exit( SpringApplication.run( BK_BatchDevApplication.class, args) ) );
    }

//    @Autowired
    private JobLauncher jobLauncher; // JobLauncher 주입

//    @Autowired
    private ApplicationContext applicationContext; // ApplicationContext 주입 (Job을 찾기 위함)

    @Override
    public void run(String... args) throws Exception
    {
        String jobName = null;
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        for (String arg : args)
        {
            if (arg.startsWith("--spring.batch.job.names="))
            {
                jobName = arg.substring("--spring.batch.job.names=".length());
            }
            else
            {
                /*********************************
                 * [ Job 이름 인자를 제외한 모든 인자 처리 ]
                 * 'name=value, type, indentificationFlag' 형식 파싱
                 * 정규식: 그룹1=name, 그룹2=value , 그룹3=type(선택), 그룹4=idenfificationFlag(선택)
                 * ex) targetCount=5,java.lang.Integer,false
                 *********************************/
                int equalsIndex = arg.indexOf("=");

                if (equalsIndex > 0) {
                    String key = arg.substring(0, equalsIndex);
                    String valueWithType = arg.substring(equalsIndex + 1);

                    // "value,type,identifying" 형식에서 "value"만 추출
                    String actualValue;
                    int commaIndex = valueWithType.indexOf(",");
                    if (commaIndex > 0) {
                        actualValue = valueWithType.substring(0, commaIndex);
                    } else {
                        actualValue = valueWithType;
                    }

                    // Jasypt 관련 인자는 제외
                    if (!key.startsWith("jasypt.")) {
                        log.debug("Adding job parameter: {} = {}", key, actualValue); // 디버깅용 로그 추가
                        jobParametersBuilder.addString(key, actualValue);
                    }
                }
                // -- 로 시작하지 않는 인자나 = 이 없는 인자는 무시합니다.
            }
        }

        // Job 이름이 명시되지 않았거나 "NONE"이면 Job을 실행하지 않음
        if (jobName == null || "NONE".equalsIgnoreCase(jobName)) {
            log.error("No specific job name provided or set to NONE. Exiting without running a batch job.");
            return;
        }

        log.info("Attempting to launch Spring Batch Job: {}", jobName);

        try {
            /*********************************
             * [ getBeansOfType(Job.class) 호출 ]
             * => 1. runBatchJobsOnStartUp() 메서드가 ApplicationReadyEvent에 의해 trigger 될 때, applicationContext.getBeansOfType(Job.class) 가 호출됨.
             * => 2. ApplicationContext 는 자신이 관리하고 있는 모든 등록된 Bean을 내부적으로 순회.
             * => 3. 각 Bean에 대해, 해당 Bean의 Type 이 Job.class(또는 Job.class를 구현하거나 상속한 타입) 인지 확인
             * => 4. 타입이 일치하는 모든 Bean을 찾음.
             *********************************/
            Job job = applicationContext.getBean(jobName, Job.class);

            // JobInstance 충돌 방지를 위해 'run.id' 파라미터 추가
            if (jobParametersBuilder.toJobParameters().getString("run.id") == null) {
                jobParametersBuilder.addLong("run.id", System.currentTimeMillis());
            }

            JobParameters jobParameters = jobParametersBuilder.toJobParameters();

            log.info("Launching job '{}' with parameters: {}", job.getName(), jobParameters);
            jobLauncher.run(job, jobParameters);
            log.info("Job '{}' completed successfully.", job.getName());

        } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
            log.error("Job with name '{}' not found in the ApplicationContext. Please check the job bean name.", jobName);
            log.error("Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error running Spring Batch Job: {}", e.getMessage(), e);
            throw e;
        }
    }
}
