# Spring Batch 대용량 CSV 파일 처리
- 이 프로젝트는 Spring Batch를 사용하여 대용량 CSV 파일을 처리하는 예제입니다. 이 예제에서는 CSV 파일을 읽고, 데이터를 변환한 후, 데이터베이스에 저장하는 작업을 수행합니다. 파티셔닝 기능을 통한 병렬 처리, 로깅, 예외 처리 등의 기능을 포함하고 있습니다.

## 기술 스택
- **Java 17**, **Spring Boot 3.5**, **Spring Batch 5.2**, **MySQL Database**

## 프로젝트 구조
- 배치 플로우 구조
  - Start -> DeleteStep -> MasterStep -> SlaveStep -> End
- 주요 컴포넌트
  - Master Step: 파티션 분할 및 작업 분배
  - Slave Step: 실제 데이터 처리 (병렬 실행)
  - Custom Reader: CSV 라인 범위별 데이터 읽기
  - Item Writer: 데이터베이스 저장
  - Execution Listener: 배치 실행 모니터링

## 기능
- 파티셔닝 기반 병렬 처리
  - 분할 단위: 1,000라인 기준으로 파티션 분할
  - 청크 크기: 100개 단위로 처리
  - Grid Size: 10개 병렬 스레드
  - Task Executor: SimpleAsyncTaskExecutor 사용
- 내결함성 및 재시도 로직
  - 재시도 횟수: 최대 3회
  - 재시도 대상: SQLException
  - Fault Tolerant: 활성화
- CSV 파싱 최적화
- Custom Tokenizer: 따옴표 내 콤마 무시 처리
- 인코딩: UTF-8
- 스킵 라인: 헤더 1라인 제외

## 실행 방법
1. **MySQL 데이터베이스 설정**
   - `application.yml` 파일에서 MySQL 데이터베이스 연결 정보 설정
   - `docker-compose.yml` 파일을 사용하여 MySQL 컨테이너 실행 가능
   - resource/sql/ddl.sql 파일을 실행하여 테이블 생성
   - mysql 8.0 이상 버전 사용
2. **프로젝트 빌드 및 실행**
   - `./gradlew build`
   - IDE에서 실행하거나, JAR 파일로 실행
   - `java -jar build/libs/demo-0.0.1-SNAPSHOT.jar --job.name=importJob baseDt=20241201`
   - `baseDt` 파라미터는 실행 날짜로, YYYYMMDD 형식으로 입력
   - `job.name` 파라미터는 실행할 배치 작업 이름으로, 기본값은 `importJob`
