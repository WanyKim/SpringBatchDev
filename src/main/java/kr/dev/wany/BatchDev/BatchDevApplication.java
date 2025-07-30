package kr.dev.wany.BatchDev;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBatchProcessing  // Spring Batch 인프라 활성화
@ComponentScan( basePackages = {"kr.dev.wany.BatchDev", "kr.dev.wany.BatchDev.batchTest"} )
public class BatchDevApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger( BatchDevApplication.class );

	public static void main(String[] args) {
		// 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달하기 위함.
		System.exit( SpringApplication.exit( SpringApplication.run(BatchDevApplication.class, args) ) );
	}

	@Autowired
	private JobLauncher jobLauncher; // JobLauncher 주입

	@Autowired
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
			{ // Job 이름 인자를 제외한 모든 인자 처리
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
