package kr.dev.wany.BatchDev;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableBatchProcessing  // Spring Batch 인프라 활성화
@ComponentScan( basePackages = {"kr.dev.wany.BatchDev.batchTest", "kr.dev.wany.BatchDev.batchTest.step_3.bootBatch"})
public class BatchDevApplication {

	public static void main(String[] args) {
		
//		SpringApplication.run(BatchDevApplication.class, args);

		// 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달하기 위함.
		System.exit( SpringApplication.exit( SpringApplication.run(BatchDevApplication.class, args) ) );
	}


	/**
	 * 웹 서버 구동 없는 배치 애플리케이션에서
	 * Program Argument를 통해 Job을 실행하기 위한 JobLauncherApplicationRunner 빈 수동 등록.
	 * 이 러너가 커맨드 라인 인자의 `--spring.batch.job.names`를 자동으로 파싱하여 해당 Job을 실행합니다.
	 * (Spring Batch 5.x 부터 setJobNames() 메서드는 더 이상 사용되지 않습니다.)
	 */
	@Bean
	public JobLauncherApplicationRunner jobLauncherApplicationRunner(
			JobLauncher jobLauncher,
			JobExplorer jobExplorer,
			JobRepository jobRepository
	) {
		JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
		return runner;
	}


//	/**
//	 * Program Argument로 전달된 Job 이름에 따라 Job을 실행하는 JobLauncherApplicationRunner를 정의합니다.
//	 * --spring.batch.job.names 인자가 없으면 어떤 Job도 실행하지 않습니다.
//	 */
//	@Bean
//	public JobLauncherApplicationRunner jobLauncherApplicationRunner(
//			JobLauncher jobLauncher,
//			JobExplorer jobExplorer,
//			JobRepository jobRepository,
//			// Program Argument에서 spring.batch.job.names 값을 주입받습니다.
//			// 기본값으로 빈 문자열을 설정하여 인자가 없을 경우를 대비합니다.
//			@Value("${spring.batch.job.names:}") String jobNamesFromArgs // ⭐핵심: Program Argument 값 주입⭐
//	) {
//		JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner( jobLauncher, jobExplorer, jobRepository );
//
//		// --spring.batch.job.names 인자가 제공되었는지 확인합니다.
//		if ( !jobNamesFromArgs.isEmpty() ) {
//			// 인자가 있다면, 쉼표로 구분된 Job 이름을 Set<String>으로 변환합니다.
//			Set<String> jobNamesToRun = Arrays.stream( jobNamesFromArgs.split( "," ) ).map( String::trim ).filter( name -> !name.isEmpty() ).collect( Collectors.toSet() );
//		}
//
//		return runner;
//	}
}
