package kr.dev.wany.BatchDev.batchTest.section_5.taskletProc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/************************************
 * Name : ZombieProcessCleanupTasklet
 * To-Do : 
 * Developer : twkim
 * Date : 2025-07-30 오전 11:06
 ************************************/
public class ZombieProcessCleanupTasklet implements Tasklet
{
    private static final Logger logloc = LoggerFactory.getLogger( ZombieProcessCleanupTasklet.class );
    
    private final int processToKill = 10;
    private int killedProcesses = 0;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        killedProcesses ++;
        
        logloc.info( "프로세스 강제종료... ({}/{})", killedProcesses, processToKill );
        
        if( killedProcesses >= processToKill )
        {
            logloc.info( "시스템 안정화 완료. 모든 Zombie process 제거" );
            
            // STEP 종료 처리.
            return RepeatStatus.FINISHED;
        }
        
        // STEP 추가 실행
        return RepeatStatus.CONTINUABLE;
    }
}
