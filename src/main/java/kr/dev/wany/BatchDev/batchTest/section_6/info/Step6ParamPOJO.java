package kr.dev.wany.BatchDev.batchTest.section_6.info;

import lombok.Getter;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/************************************
 * Name : Step6ParamPOJO
 * To-Do : 
 * Developer : twkim
 * Date : 2025-08-01 오후 5:46
 ************************************/
@StepScope  // Job parameter을 받기위한 Annotation
@Getter
@Component  // Bean 등록
public class Step6ParamPOJO {

    // 필드에 직접 주입
    @Value( "#{jobParameters[missionName]}" )
    private String missionName;

    // setter 메서드 사용
    private int securityLevel;

    // 생성자 parameter 로 주입
    private final String operationCommander;

    public Step6ParamPOJO( @Value( "#{jobParameters[operationCommander]}" ) String operationCommander ) {
        this.operationCommander = operationCommander;
    }

    @Value( "#{jobParameters[securityLevel]}" )
    public void setSecurityLevel( int securityLevel ) {
        this.securityLevel = securityLevel;
    }
}
