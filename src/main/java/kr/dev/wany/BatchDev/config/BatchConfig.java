package kr.dev.wany.BatchDev.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.configuration.support.JobRegistrySmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/************************************ 
 * Name : BatchConfig
 * To-Do : 
 * Developer : twkim
 * Date : 2025-08-01 오후 1:04
 ************************************/
@Configuration
@EnableBatchProcessing
public class BatchConfig
{
    /*********************************
     * @EnableBatchProcession 을 별도 @Configuration 으로 분리한 이유는
     * Spring Boot 가 application을 시작할 때 핵심 기능(AOP, Proxy등...) 을 먼저 안전하게 초기화 할수 있기 위함.
     * 그 후, 분리된 BatchConfig 이 로드되면서 Spring Batch 관련 Bean 들이 올바른 순서에 맞게 등록된다.
     *
     * 이로써 Bean 초기화 순서 충돌이 해결되고, jobRepository 경고가 사라지며, Spring Boot의 기본
     * JobLauncherApplicationRunner 가 Job Bean 들을 정상으로 인식하게 된다.
     *********************************/
//    @Bean
//    public static BeanDefinitionRegistryPostProcessor jobRegistryBeanPostProcessorRemover() {
//        return registry -> registry.removeBeanDefinition("jobRegistryBeanPostProcessor");
//    }
//
//    @Bean
//    public JobRegistrySmartInitializingSingleton jobRegistrySmartInitializingSingleton(JobRegistry jobRegistry) {
//        return new JobRegistrySmartInitializingSingleton(jobRegistry);
//    }

}
