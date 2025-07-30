# 프로그램 실행 명령  
-- Path : kr/dev/wany/BatchDev/batchTest

### 1. /section_3/bootBatch/SystemTerminationConfig  
 - Program Argument : --spring.batch.job.names=firstJob   
 - Program Argument : --spring.batch.job.names=firstJob,systemTerminationSimulationJob  

### 2. /section_5/taskletProc/ZombieBatchConfig
- Program Argument : --spring.batch.job.names=zombieCleanJob  

### 3. /section_6/Step6Config
- Program Argument : --spring.batch.job.names=processSection6 terminatorId=KILL-9,java.lang.String targetCount=5,java.lang.Integer,false  
