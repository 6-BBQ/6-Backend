# rpgpt API 백엔드
## 기술 스택
Spring Boot 3.4.5  
Spring Boot JPA  
Spring Actuator  
Spring Security  
OpenAPI[Swagger] 2.8.5  
JWT 0.11.5  
MySQL 8.4  
Redis  
## 회원 기능 API 주소
```POST``` /api/auth/signup : 회원가입  
```POST``` /api/auth/login : 로그인  
```POST``` /api/auth/logout : 로그아웃  
```GET``` /api/auth/me : 현재 로그인한 회원정보

## 던파 관련 API 주소
```GET``` /api/df/search : 던파 캐릭터 검색  
```GET``` /api/df/character : 던파 캐릭터 상세조회  
```POST``` /api/characters : 회원에 모험단 혹은 캐릭터 등록  
```DELETE``` /api/characters : 회원에 속한 캐릭터 제거  
```GET``` /api/characters/adventure : 회원에 속한 캐릭터 전체 조회

## AI 관련 API 주소
```POST``` /api/df/chat : 캐릭터 AI채팅 내역  
```DELETE``` /api/df/chat : 캐릭터 AI채팅 내역 초기화  

## DB 생성
```
CREATE DATABASE gamept;
CREATE USER 'DEMOUS'@'localhost' IDENTIFIED BY '12345678';
GRANT ALL PRIVILEGES ON gamept.* TO 'DEMOUS'@'localhost';
FLUSH PRIVILEGES;
```

## swagger API 주소
http://localhost:8080/swagger-ui/index.html


