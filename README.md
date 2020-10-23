# StudyHahoho

## 1. 프로젝트 소개

지역별 주제별 스터디 모임을 만들고, 설정에 따라 메일 및 웹 알림을 받을 수 있는 

스터디 & 스터디 모임 관리 사이트



## 2. 설치

### 2.1. IDE

IDE에서 프로젝트를 import한 후, 메이븐을 통해 컴파일 빌드를 하고 App.java 클래스 실행

#### Maven으로 컴파일 빌드

메이븐이 설치되어 있지 않은 경우 메이븐 랩퍼(mvnw 또는 mvnw.cmd(윈도)를 사용해서 빌드하세요.

```
mvnw compile
```



### 2.2. 콘솔

JAR 패키징 후 java -jar로 실행

```
mvnw clean compile package

java -jar target/*.jar
```



### 2.3. DB 설정

PostgreSQL 설치 후, psql로 접속해서 아래 명령어 사용하여 DB와 USER 생성하고 권한 설정.

```sql
CREATE DATABASE testdb;
CREATE USER testuser WITH ENCRYPTED PASSWORD 'testpass';
GRANT ALL PRIVILEGES ON DATABASE testdb TO testuser;
```



## 3. 사용 기술

| Category        | skill            |
| --------------- | ---------------- |
| Backend         | Springboot 2.3.3 |
| Template Engine | Thymleaf         |
| DB              | Spring Data JPA  |
| Frontend        | bootstrap        |
| Script          | javascript       |



## 4. TODO

dockerfile을 통한 개발환경 세팅