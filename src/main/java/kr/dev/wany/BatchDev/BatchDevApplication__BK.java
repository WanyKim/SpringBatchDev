package kr.dev.wany.BatchDev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//@SpringBootApplication
//@EnableBatchProcessing  // Spring Batch 인프라 활성화
//@ComponentScan( basePackages = {"kr.dev.wany.BatchDev.batchTest", "kr.dev.wany.BatchDev.batchTest.step_3.bootBatch"})  // Main 함수 하위에 Job 이 있어 ComponentScan 불필요
public class BatchDevApplication__BK {

//	private static final Logger logloc = LoggerFactory.getLogger( BatchDevApplication__BK.class );
//
//	private final JobLauncher jobLauncher;
//	private final ApplicationContext applicationContext;
//	private final String[] applicationArguments; // Application 의 원본 인자를 받기
//
//	// Program Argument에서 Job 이름을 직접 주입
//	@Value("${spring.batch.job.names:}") // 기본값은 빈 문자열
//	private String jobNamesFromArgs;
//
//	@Autowired
//	public BatchDevApplication__BK(JobLauncher jobLauncher, ApplicationContext applicationContext, String[] applicationArguments) {
//		this.jobLauncher = jobLauncher;
//		this.applicationContext = applicationContext;
//		this.applicationArguments = applicationArguments;
//	}
//
//	public static void main(String[] args) {
//		// 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달하기 위함.
//		System.exit( SpringApplication.exit( SpringApplication.run( BatchDevApplication__BK.class, args) ) );
//	}
//
//
//	/**
//     * @EventListener 사용
//	 * ApplicationReadyEvent는 스프링 애플리케이션 컨텍스트가 완전히 초기화된 후 발생.  이 시점에 Job 실행 로직을 수행
//	 */
//	@EventListener(ApplicationReadyEvent.class)
//	public void runBatchJobsOnStartup() {
//		/*********************************
//		 * [1] Job 이름이 Program Argument로 제공되지 않았다면 (빈 문자열), 아무 Job도 실행하지 않고 종료
//		 *     `JobLauncherApplicationRunner`의 기본 "모든 Job 실행" 동작을 방지
//		 *********************************/
//		if (jobNamesFromArgs == null || jobNamesFromArgs.isEmpty()) {
//			logloc.error( "[ERROR] No Spring Batch job names provided in arguments (--spring.batch.job.names). Exiting without running any job." );
//			// 여기서 System.exit(0)을 호출하면 애플리케이션이 종료됨.
//			// 단, SpringApplication.exit()은 main 메서드에서 이미 처리하고 있으므로, 여기서는 return만으로 Job 실행을 bypass
//			return;
//		}
//
//		// 쉼표로 구분된 Job 이름을 파싱합니다.
//		Set<String> requestedJobBeanNames = Arrays.stream(jobNamesFromArgs.split(","))
//				.map(String::trim)
//				.filter(name -> !name.isEmpty())
//				.collect(Collectors.toSet());
//
//		if (requestedJobBeanNames.isEmpty()) {
//			logloc.error( "[ERROR] No valid job names found in --spring.batch.job.names argument. Exiting." );
//			return;
//		}
//
//		logloc.info( "Attempting to run jobs based on requested bean names: {}", requestedJobBeanNames );
//
//		/*********************************
//		 * ApplicationContext에 등록된 모든 Job 타입의 bean을 찾아 Map 형태로 변환.
//		 * Key : Job bean 의 이름
//		 * [ Tips. ]
//		 * ApplicationContext 는 container 이며, application 시작 시 개발자가 정의한 모든
//		 * @Component, @Service, @Repository, @Controller, @Configuration 클래스 및 @Bean 메서드를 스캔하고,
//		 * 이들을 생성하는 객체들을 singleton 으로 관리하며 bean 으로 등록한다.
//		 *
//		 * [ Bean 정의 & Scan & regist ]
//		 * 1. application이 시작될 때 Spring 은 BatchDevApplication 의 @ComponentScan 이 지정한 패키지를 스캐.
//		 * 2. 이 패키지 안에 있는 @Configuration 클래스(Ex: SystemTerminationConfig.java) 를 찾아 @Bean 이 붙어 있는 메서드를 분석.
//		 * 3. 각 @Bean 메서드가 반환하는 Job 객체들을 생성 후 Spring Context 내부에 bean 으로 등록.
//		 *    이때 bean 의 이름은 @Bean 에 지정된 이름 (@Bean("test") 의 "test") 가 되고, 타입은 Job 인터페이스를 구현한 클래스가 된다.
//		 *    만약 @Bean 에 이름을 지정하지 않으면, method 명이 Bean 의 이름이 된다.
//		 *
//		 * [ getBeansOfType(Job.class) 호출 ]
//		 * 1. runBatchJobsOnStartUp() 메서드가 ApplicationReadyEvent에 의해 trigger 될 때, applicationContext.getBeansOfType(Job.class) 가 호출됨.
//		 * 2. ApplicationContext 는 자신이 관리하고 있는 모든 등록된 Bean을 내부적으로 순회.
//		 * 3. 각 Bean에 대해, 해당 Bean의 Type 이 Job.class(또는 Job.class를 구현하거나 상속한 타입) 인지 확인
//		 * 4. 타입이 일치하는 모든 Bean을 찾음.
//		 * 5. 찾아낸 Job 타입의 Bean들을 Bean의 이름(String) 을 키로, 실제 Job 객체를 값으로 하는 Map<String, Job> 형태로 구성 후 반환.
//		 *********************************/
//		Map<String, Job> allJobBeans = applicationContext.getBeansOfType(Job.class);
//
//
//		/*********************************
//		 * [2] JobParameterBuilder 초기화 및 파라미터 추가(JobInstance 식별용)
//		 *********************************/
//		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
//
//		// 고유 식별자 추가(Job Instance 생성에 필수적인 요소)
//		jobParametersBuilder.addString( "jobId", "auto-run-" + System.currentTimeMillis() );
//
//		// 실행시간 추가
//		jobParametersBuilder.addString( "runTime", LocalDateTime.now().toString() );
//
//
//		/*********************************
//		 * [3] Job Parameter 파싱 & 타입별 로직
//		 *********************************/
//		for( String argument : applicationArguments ) {
//			// --로 시작하는 Spring boot 자체 argument는 건너띔( JobName 이기 때문)
//			if( argument.startsWith( "--" ) ) {
//				continue;
//			}
//
//
//			/*********************************
//			 * 'name=value, type, indentificationFlag' 형식 파싱
//			 * 정규식: 그룹1=name, 그룹2=value , 그룹3=type(선택), 그룹4=idenfificationFlag(선택)
//			 * ex) targetCount=5,java.lang.Integer,false
//			 *********************************/
//			String[] parts = argument.split( "=", 2 );  // '=' 기분으로 분리.
//
//			if( parts.length != 2 )
//			{
//				logloc.error( "[ERROR] Skipping unrecognized argument format(missing '='): {}", argument );
//				continue;
//			}
//
//			String paramName = parts[0].trim();
//			String valueAndTypeAndFlag = parts[1].trim();
//
//			String paramValue = null;
//			String paramType = null;  // 기본값은 String
//			boolean identificationFlag = true;  // 기본값은 true
//
//			// 쉼표(,) 기준으로 최대 3개 분리
//			String[] valueParts = valueAndTypeAndFlag.split( ",", 3 );
//
//			paramValue = valueParts[0].trim();  // 0번지는 항상 값.
//
//			if( valueParts.length > 1 ) {  // Type 값이 있을 경우.
//				paramType = valueParts[1].trim();
//			}
//
//			if( valueParts.length > 2 ) { // identificationFlag 값이 있을 경우.
//				identificationFlag = Boolean.parseBoolean( valueParts[2].trim() );
//			}
//
//			logloc.info( "Processing parameter: Name={}, Value={}, Type={}, Identifying={}", paramName, paramValue, (paramType == null ? "String(default)" : paramType), identificationFlag );
//
//			try {
//				// 타입에 따라 적절한 add 메서드 호출
//				if (paramType == null || paramType.isEmpty() || "string".equalsIgnoreCase(paramType) || "java.lang.String".equalsIgnoreCase(paramType))
//				{
//					jobParametersBuilder.addString(paramName, paramValue, identificationFlag);
//				}
//				else if ("long".equalsIgnoreCase(paramType) || "java.lang.Long".equalsIgnoreCase(paramType))
//				{
//					jobParametersBuilder.addLong(paramName, Long.valueOf(paramValue), identificationFlag);
//				}
//				else if ("double".equalsIgnoreCase(paramType) || "java.lang.Double".equalsIgnoreCase(paramType))
//				{
//					jobParametersBuilder.addDouble(paramName, Double.valueOf(paramValue), identificationFlag);
//				}
//				else if ("date".equalsIgnoreCase(paramType) || "java.util.Date".equalsIgnoreCase(paramType))
//				{
//					// LocalDateTime 파싱을 기본으로 사용, 필요 시 DateFormatter 활용
//					jobParametersBuilder.addDate(paramName, java.sql.Timestamp.valueOf(LocalDateTime.parse(paramValue)), identificationFlag);
//				}
//				else
//				{
//					logloc.error( "[ERROR] Unrecognized parameter type specified: '{}' for '{}'. Defaulting to String ", paramType, paramName );
//					jobParametersBuilder.addString(paramName, paramValue, identificationFlag);
//				}
//			} catch (NumberFormatException e)
//			{
//				logloc.error( "[ERROR] parsing numeric value for parameter '{}':{}. Default to String E: {} ", paramName, paramValue, e.getMessage() );
//				jobParametersBuilder.addString(paramName, paramValue, identificationFlag);
//			}
//			catch (java.time.format.DateTimeParseException e)
//			{
//				logloc.error( "[ERROR] parsing date value for parameter  '{}':{}. Default to String E: {} ", paramName, paramValue, e.getMessage() );
//				jobParametersBuilder.addString(paramName, paramValue, identificationFlag);
//			}
//			catch (Exception e)
//			{
//				logloc.error( "[ERROR] Unexpected error adding parameter  '{}': E: {} ", paramName, e.getMessage() );
//				jobParametersBuilder.addString(paramName, paramValue, identificationFlag);
//			}
//		}
//
//
//		/*********************************
//		 * [4] 각 Job 실행
//		 *********************************/
//		for (String requestedBeanName : requestedJobBeanNames) {
//			Job jobToRun = allJobBeans.get(requestedBeanName);
//
//			if (jobToRun == null) {
//				logloc.error( "[ERROR] Job bean with name '{}' not found in application context. SKIPPING.", requestedBeanName );
//				continue;
//			}
//
//			// Bean에 등록된 Job name
//			String actualJobName = jobToRun.getName();
//
//
//			/*********************************
//			 * 1. Job Parameters 생성 (매번 고유한 Job 실행을 위해)
//			 * 2. Spring Batch는 동일한 Job 이름과 동일한 JobParameters로는 Job을 두 번 실행하지 않음.
//			 * 3. 따라서 Job Parameters에 유니크한 값을 넣어줘야 매번 새로운 Job 인스턴스로 실행
//			 *********************************/
////			JobParameters jobParams = new JobParametersBuilder()
////					.addString("jobId", actualJobName + "-" + System.currentTimeMillis())
////					.addString("runTime", LocalDateTime.now().toString())
////					.toJobParameters();
//
//			// 위 적용건은 JobParameter 을 사용하지 않을때 생성된 부분
//			JobParameters jobParams = jobParametersBuilder.toJobParameters();
//
//			try
//			{
//				logloc.info( "---> Starging job: {} , (Bean: {}} <---", actualJobName, requestedBeanName );
//
//				jobLauncher.run(jobToRun, jobParams);
//
//				logloc.info( "---> Job: {}, Finished Successfully <---", actualJobName );
//			}
//			catch (Exception e)
//			{
//				logloc.error( "[ERROR] running job '{}' (Bean: {}): E: {}", actualJobName, requestedBeanName, e.getMessage() );
//				e.printStackTrace();
//			}
//		}
//	}

}
