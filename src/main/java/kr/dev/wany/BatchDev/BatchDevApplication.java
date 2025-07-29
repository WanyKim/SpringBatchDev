package kr.dev.wany.BatchDev;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBatchProcessing  // Spring Batch 인프라 활성화
@ComponentScan( basePackages = {"kr.dev.wany.BatchDev.batchTest", "kr.dev.wany.BatchDev.batchTest.step_3.bootBatch"})
public class BatchDevApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(BatchDevApplication.class, args);

		// 배치 작업의 성공/실패 상태를 exit code로 외부 시스템에 전달하기 위함.
//		System.exit( SpringApplication.exit( SpringApplication.run(BatchDevApplication.class, args) ) );
	}


}
