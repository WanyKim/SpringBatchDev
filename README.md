# 프로그램 실행 명령  
-- Path : kr/dev/wany/BatchDev/batchTest

### 1. /section_3/bootBatch/SystemTerminationConfig  
 - Program Argument : --spring.batch.job.names=firstJob   
 - Program Argument : --spring.batch.job.names=firstJob,systemTerminationSimulationJob  

### 2. /section_5/taskletProc/ZombieBatchConfig
- Program Argument : --spring.batch.job.names=zombieCleanJob  

### 3. /section_6
- /Step6ParamConfig   
-- Program Argument : --spring.batch.job.names=processSection6 terminatorId=KILL-9 targetCount=5
  

- /Step6ParamDateConfig  
-- Program Argument : --spring.batch.job.names=Step6ParamDateJob executionDate=2024-01-01 startTime=2024-01-01T14:30:00


- /Step6ParamConfig & /Step6ParamDateConfig 모두 실행
-- Program Argument : --spring.batch.job.names=processSection6,Step6ParamDateJob terminatorId=KILL-9 targetCount=5 executionDate=2024-01-01 startTime=2024-01-01T14:30:00  


- /Step6ParamEnumConfig
  -- Program Argument : --spring.batch.job.names=step6ParamEnumJob questDifficulty=HARD(package kr.dev.wany.BatchDev.batchTest.section_6.info.QuestDifficulty)