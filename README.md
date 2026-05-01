# playground-10th project                                                                                                                                                                                                                     
                                   
  JWT 토큰 기반 로그인/인증 기능 학습 프로젝트                                                                                                                                                                                          
                                                                                                                                                                                                                                        
  ## 기술 스택                                                                                                                                                                                                                          
                                                                                                                                                                                                                                        
  - Java 25 / Spring Boot 4.0.6                                                                                                                                                                                                         
  - Spring Security
  - MySQL 8.0                                                                                                                                                                                                                           
  - Redis                                                                                                                                                                                                                               
  - Thymeleaf
  - Docker Compose

  ## 개발 도구

  - Swagger (API 문서)                        
  - H2 (로컬 인메모리 DB)                                                                                                                                                                                                                     
                  
  ## 주요 기능

  - 회원가입 / 로그인                                                                                                                                                                                                                   
  - JWT accessToken + refreshToken 발급 (HttpOnly 쿠키)
  - accessToken 만료 시 자동 재발급                                                                                                                                                                                                     
  - 로그아웃 (Redis refreshToken 삭제)
                                                                                                                                                                                                                                        
  ## 인증 흐름    
                                                                                                                                                                                                                                        
  로그인 → accessToken(1시간) + refreshToken(7일) 발급                                                                                                                                                                                  
         → 두 토큰 모두 HttpOnly 쿠키로 저장
         → refreshToken은 Redis에도 저장                                                                                                                                                                                                
                  
  요청 → JwtAuthenticationFilter가 쿠키에서 accessToken 검증                                                                                                                                                                            
                  
  accessToken 만료 → /api/v1/users/refresh 자동 호출                                                                                                                                                                                    
                 → Redis에서 refreshToken 검증 → 새 accessToken 발급
                                                                                                                                                                                                                                        
  로그아웃 → Redis에서 refreshToken 삭제 + 쿠키 만료                                                                                                                                                                                    
                                                                                                                                                                                                                                        
  ## 프로젝트 구조                                                                                                                                                                                                                      
                  
  src/main/java
  ├── domain/user
  │   ├── controller   # UserController, PageController
  │   ├── service      # UserService                                                                                                                                                                                                    
  │   ├── dto          # 요청/응답 DTO
  │   ├── model        # User 엔티티                                                                                                                                                                                                    
  │   └── repository   # UserRepository
  ├── infra                                                                                                                                                                                                                             
  │   ├── security     # SecurityConfig, JwtAuthenticationFilter, CookieProvider                                                                                                                                                        
  │   │   └── jwt      # JwtPlugin, JwtAuthenticationToken
  │   └── redis        # RefreshTokenService                                                                                                                                                                                            
  └── common           # GlobalExceptionHandler, ErrorResponse
    
  | 클래스 | 역할 |                                                                                                                                                                                                                     
  |---|---|       
  | `UserController` | user(회원가입, 로그인, 로그아웃, 토큰 재발급) API |
  | `PageController` | Thymeleaf 페이지 라우팅 (login, signup, home) |                                                                                                                                                                  
  | `UserService` | user(회원가입/로그인/로그아웃) 비즈니스 로직 |                                                                                                                                                                            
  | `UserRepository` | user 테이블과 통신 layer |                                                                                                                                                                                             
  | `User` | user 엔티티 (id, email, password, role) |                                                                                                                                                                                  
  | `JwtPlugin` | JWT 토큰 발급 및 검증 |                                                                                                                                                                                               
  | `JwtAuthenticationFilter` | 요청마다 쿠키에서 accessToken 추출 및 검증 |                                                                                                                                                            
  | `JwtAuthenticationToken` | 검증된 유저 정보를 SecurityContext에 저장하는 인증 객체 |                                                                                                                                                
  | `SecurityConfig` | Spring Security 필터 체인 설정 |                                                                                                                                                                                 
  | `CookieProvider` | accessToken/refreshToken 쿠키 생성 및 삭제 |                                                                                                                                                                     
  | `CustomAuthenticationEntryPoint` | 인증 실패 시 401 JSON 응답 |                                                                                                                                                                     
  | `RefreshTokenService` | Redis에 refreshToken 저장/검증/삭제 |                                                                                                                                                                       
  | `GlobalExceptionHandler` | 전역 예외 처리 |                                                                                                                                                                                         
  | `UserPrincipal` | 인증된 유저 정보 객체 |  
                                                                                                                                                                                                                                        
  ## 실행 방법                                                                                                                                                                                                                          
   
  ```bash                                                                                                                                                                                                                               
  # 1. Docker 컨테이너 실행 (MySQL + Redis)
  docker-compose up -d                                                                                                                                                                                                                  
   
  # 2. 애플리케이션 실행                                                                                                                                                                                                                
  ./gradlew bootRun

  접속: http://localhost:8080   
  ```

  ## 실행 결과 사진 

  ### 1 정적페이지(inext.html)
  
<img width="1802" height="1466" alt="image" src="https://github.com/user-attachments/assets/85bc9638-e656-41d3-8d10-b254e8d6997c" />


### 2. 회원가입 페이지(signup.html)
<img width="1802" height="1468" alt="image" src="https://github.com/user-attachments/assets/f85a061d-0201-46db-9b17-3038373fec85" />

### 3. 로그인 페이지(login.html)

<img width="1798" height="1456" alt="image" src="https://github.com/user-attachments/assets/e5373d89-ded4-4473-a2a2-e70be4122e73" />

### 4. 로그인 성공 후 페이지(home.html)

<img width="1798" height="1456" alt="image" src="https://github.com/user-attachments/assets/450f3116-fc5c-4444-8919-cc807980120a" />


### 5. 로그 아웃(login.html)
 다시 로그인 페이지로 리다이렉트 된다.





  
  
