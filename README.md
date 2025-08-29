<img width="2233" height="1863" alt="image" src="https://github.com/user-attachments/assets/e51b2ee9-fb98-4c69-9964-c894d03d689d" /># OurHour (아워-아워)

> 개발자를 위한, 개발자에 의한 올인원 협업 플랫폼 'OurHour'입니다. 프로젝트 및 세부 마일스톤, 이슈 관리, 실시간 커뮤니케이션까지, 개발팀의 모든 작업을 하나의 플랫폼에서 해결하여 생산성을 극대화합니다.

<br/>

[OurHour 바로가기](https://www.ourhour.cloud/)

## 📖 프로젝트 소개 (Overview)

개발팀의 소통과 생산성 저하는 많은 비용을 발생시킵니다. 흩어져 있는 업무 툴들(Jira, Slack, Google Docs 등)을 오가며 생기는 비효율, 파편화된 정보로 인한 혼란을 해결하고자 'OurHour'를 기획했습니다.

OurHour는 **기획, 개발, 배포, 운영에 이르는 개발의 전 과정을 하나의 플랫폼에서 관리**하여, 팀원들이 오직 개발에만 집중할 수 있는 환경을 제공하는 것을 목표로 합니다.

<br/>

## ✨ 주요 기능

- **🔐 사용자 인증**: JWT 기반의 안전한 자체 회원가입, 로그인, 비밀번호 찾기 기능, 소셜 로그인(Google, GitHub 등) 지원
- **🏢 조직 및 멤버 관리**: 이메일 기반의 인증 및 초대로 간편한 팀원 추가 및 역할 기반의 체계적인 권한 관리, 깃허브 연동
- **🚀 프로젝트 관리**: 프로젝트 생성/수정/삭제, 마일스톤 기반의 진행 상황 추적, 이슈 트래킹
- **✍️ 게시판**: 공지사항, 자유게시판 등 목적별 정보 공유 및 논의
- **💬 실시간 채팅**: 개인, 1:1 및 그룹 채팅을 통한 빠르고 원활한 커뮤니케이션
- **🤖 AI 챗봇**: 내 문서와 데이터를 학습한 AI 챗봇을 통해 회사 정보, 프로젝트 관련 지식, 업무 프로세스를 손쉽게 검색 및 확인
<br/>

## 🛠️ 기술 스택 (Tech Stack)

| 구분           | 항목        | 기술/도구                                             |
| ------------ | --------- | ------------------------------------------------- |
| **백엔드**      | 언어        | Java                                              |
|              | 프레임워크     | Spring Boot                                       |
|              | 데이터베이스    | MySQL, Spring Data JPA(ORM), Flyway(DB Migration) |
|              | 캐시        | Redis                                             |
|              | 실시간 통신    | WebSocket, STOMP                                  |
|              | 인증 및 보안   | JWT, Spring Security                              |
|              | 모니터링      | Grafana, Prometheus                               |
| **프론트엔드**    | 언어        | TypeScript                                        |
|              | 프레임워크     | React                                             |
|              | 상태 관리     | Redux                                             |
|              | 서버 상태 관리  | TanStack Query(React Query)                       |
|              | 라우팅       | TanStack Router                                   |
|              | 통신        | StompJS                                           |
|              | 스타일링 & UI | Tailwind CSS, shadcn/ui                           |
|              | 빌드 도구     | Vite                                              |
| **배포 및 인프라** | 클라우드      | AWS EC2, GCP                                      |
|              | 스토리지      | S3                                                |
|              | 컨테이너      | Docker                                            |
|              | CI/CD     | GitHub Actions                                    |
| **LLM**      | 언어        | Python                                            |
|              | 데이터베이스    | Cloud SQL                                         |
|              | 프레임워크/도구  | LangChain, OpenAI                                 |


### 문서 관리 도구 (Tools)
- **협업 및 문서:** Github, Notion
- **디자인 및 프로토타이핑:** Figma
- **데이터베이스 모델링:** erdCloud

<br/>

## 🏗️ 아키텍처 (Architecture)

<img width="2233" height="1863" alt="image" src="https://github.com/user-attachments/assets/4276ddec-a62b-4ad3-9a30-1ada2aeda293" />
> 사용자의 요청은 React 기반의 프론트엔드에서 시작하여, Spring Boot 백엔드 서버로 전달됩니다. 서버는 비즈니스 로직을 처리한 후 MySQL 데이터베이스와 통신하여 데이터를 영속화합니다. 인증/인가는 JWT 토큰을 통해 이루어지며, 실시간 채팅 기능은 WebSocket을 활용합니다.

<br/>

## 💾 ERD (Database Schema)

<img width="1195" height="817" alt="image" src="https://github.com/user-attachments/assets/1e6d7cbd-7e35-40e4-9860-eacf64e04c31" />

## 📁 폴더 구조

### 🏗️ Backend (Spring Boot)

```
backend/
├── src/main/java/com/domain  # 도메인별 Java 소스 코드
├── src/main/java/com/global  # 전역 Java 소스 코드
├── src/main/resources/       # 설정 파일 및 리소스
├── build.gradle             # Gradle 빌드 설정
└── Dockerfile               # Docker 이미지 설정
```

### 🎨 Frontend (React + TypeScript)

```
frontend/
├── src/
│   ├── components/          # UI 컴포넌트
│   ├── assets/              # 이미지 등
│   ├── pages/              # 페이지 컴포넌트
│   ├── api/                # API 통신 모듈
│   ├── hooks/              # 커스텀 훅
│   ├── lib/                # 외부 라이브러리
│   ├── constants/          # 상수
│   ├── routes/             # 라우트 경로
│   ├── stores/             # 상태 관리 (Redux)
│   ├── styles/              # 디자인을 위한 css파일 등
│   ├── types/              # TypeScript 타입 정의
│   └── utils/              # 유틸리티 함수
├── package.json            # Node.js 의존성
└── vite.config.ts         # Vite 빌드 설정
```

### 🤖 LLM 서비스 (Python)

```
llm/
├── app/
│   ├── models/             # 데이터베이스 모델
│   ├── routes/             # API 엔드포인트
│   ├── services/           # 비즈니스 로직
│   └── utils/              # 유틸리티 함수
├── requirements.txt         # Python 의존성
└── Dockerfile              # Docker 이미지 설정
```

### 🐳 인프라 및 배포

```
├── docker-compose.yml      # 배포용 Docker Compose
├── docker-compose.dev.yml  # 개발용 Docker Compose
├── monitoring/             # 모니터링 도구 (Grafana, Prometheus)
└── scripts/                # 배포 및 유틸리티 스크립트
```

<br/>


## 🚀 프로젝트 시작하기 (Getting Started)

### 로컬 실행
#### - 백엔드 + DB + 모니터링
```bash
docker-compose -f docker-compose.dev.yml up --build -d
```
#### - 프론트엔드
```bash
cd frontend
npm install
npm run dev

```
<br/>

## ✨ 상세 기능
#### - 랜딩페이지
![Image](https://github.com/user-attachments/assets/56db3b71-dd7f-43b5-860e-651e12e0b5fd)

    - 로그인 상태 시 로그아웃 가능
-----
#### - 회원가입 및 로그인 페이지
<img width="1885" height="895" alt="Image" src="https://github.com/user-attachments/assets/c328b7c2-d08c-4092-9c36-7df383257c33" />
<img width="1886" height="898" alt="Image" src="https://github.com/user-attachments/assets/5dfa380e-05bd-4b71-909a-5995700a52b8" />

    - 이메일, 비밀번호로 자체 회원가입 가능
    - 이메일 기억하기 기능 
    - 회원가입시 이메일 인증 
    - 이메일, 비밀번호 유효성 검사
    - 구글, 깃허브 소셜 로그인 가능
      
-----
#### - 비밀번호 재설정 페이지
<img width="1085" height="681" alt="image" src="https://github.com/user-attachments/assets/853aec47-9c66-4ad3-8bb6-5aedc34a89fb" />

    - 가입시 등록한 이메일을 입력해서 비밀번호 재설정 링크를 받을 수 있음

-----
#### - 개인 정보 관리 페이지
  
<img width="543" height="894" alt="image" src="https://github.com/user-attachments/assets/59168f5a-ee1e-4447-94d3-389922c643a8" />
<img width="1894" height="888" alt="image" src="https://github.com/user-attachments/assets/3c194c67-d7b2-4ed6-b97e-615ea23a1ce2" />
<img width="1898" height="889" alt="image" src="https://github.com/user-attachments/assets/6551a4f9-96a6-4b0a-a138-9896193f02e2" />
<img width="1894" height="891" alt="image" src="https://github.com/user-attachments/assets/853885c3-1bb6-4414-9142-7b97000c60d0" />


    - 우측 사이드바로 개인 정보 조회 가능
    - 비밀번호 변경 가능
    - 계정 탈퇴 가능
    - 새로운 회사 등록 가능
    - 회사별 프로필사진, 부서 및 직책 등 정보 변경 가능
    - 회사 나가기 가능

-----

#### - 회사 목록 페이지/회사 등록 모달 
<img width="1884" height="891" alt="Image" src="https://github.com/user-attachments/assets/84d2f122-e736-4a9d-ab65-5cbecfff885b" />
<img width="436" height="656" alt="Image" src="https://github.com/user-attachments/assets/e814f228-907f-4fed-8bf2-a396155b079f" />

    - 본인이 속한 회사 목록 조회 가능
    - 새로운 회사 등록 가능
    - 회사 등록 시 회사 대표 이미지, 회사명, 이메일 등 정보 업로드 가능
    - 회사 관련 페이지는 회사 구성원만 접근 가능

-----

#### - 회사 정보 페이지/회사 수정 모달/구성원 초대 모달 
  <img width="1894" height="890" alt="image" src="https://github.com/user-attachments/assets/630e78b5-3c9a-4560-bc75-1d0c10be04fc" />
  <img width="744" height="875" alt="image" src="https://github.com/user-attachments/assets/fd462040-50ef-45fc-8973-9b4a99307e32" />
  <img width="590" height="612" alt="image" src="https://github.com/user-attachments/assets/ad1b7759-cd9f-464f-8e7d-c892e8417eeb" />


    - 좌측 사이드바 회사명 옆의 아이콘 및 하단의 회사 정보 관리 버튼 누를 시 회사 정보 페이지로 이동 가능
    - 회사 정보, 회사 구성원 조회 가능
    - 회사 구성원 이름으로 검색 가능
    - 루트관리자는 회사 정보 수정 가능
    - 회사 정보 수정 모달에서 부서 및 직책 관리 가능
    - 루트관리자는 회사 삭제 가능
    - 체크박스를 누르면 구성원 내보내기 가능
    - 회사를 나가거나 계정을 탈퇴한 사람은 익명화 처리(데이터는 남아있음)
    - 루트관리자는 구성원 권한 변경 가능(게스트, 일반회원, 관리자, 루트관리자)
    - 이메일로 회사 초대 가능 및 초대 상태 확인 가능(한번에 여러명 초대 가능)

-----

#### - 조직도 페이지
  <img width="1902" height="893" alt="image" src="https://github.com/user-attachments/assets/447f57f7-e231-413a-8d57-59c08ce74de0" />

    - 회사 구성원 전체/부서별/직책별 조회 가능

-----

#### - 프로젝트 목록 페이지/프로젝트 등록 모달
<img width="1904" height="891" alt="Image" src="https://github.com/user-attachments/assets/07653ca9-a04c-4b34-9c18-2064799e5b92" />
<img width="1218" height="773" alt="image" src="https://github.com/user-attachments/assets/9d70024c-d46a-4084-8909-8a26fcc7ddb8" />

    - 회사의 모든 프로젝트 목록 조회 가능
    - 본인이 참여 중인 프로젝트만 조회 가능
    - 프로젝트명, 시작일, 종료일, 상태 정렬(오름차순, 내림차순)가능
    - 새로운 프로젝트 등록 가능
    - 프로젝트 등록 시 프로젝트명, 설명, 기간 등 정보 업로드 가능
    - 좌측 사이드바로 본인이 참여 중인 프로젝트로 이동 가능 

-----

#### - 프로젝트 대시보드 페이지
<img width="1901" height="905" alt="프로젝트대시보드" src="https://github.com/user-attachments/assets/1f7d459c-424a-4602-bc76-50ba658df071" />

    - 프로젝트의 모든 마일스톤별 이슈 조회 가능(마일스톤이 없는 경우 미분류)
    - 본인이 할당자인 이슈만 조회 가능
    - 프로젝트 구성원만 마일스톤 등록, 수정, 삭제 가능
    - 프로젝트 구성원만 이슈 등록 및 수정 페이지로 이동 가능
    - 프로젝트 구성원만 이슈 삭제 가능
    - 프로젝트 구성원만 이슈 상태만 바로 변경 가능

-----

#### - 이슈 상세페이지/이슈 등록, 수정 페이지
<img width="1890" height="893" alt="image" src="https://github.com/user-attachments/assets/f0fdb672-08d8-426c-a130-f4bd205357d3" />
  <img width="1892" height="894" alt="image" src="https://github.com/user-attachments/assets/de2f9944-1c2c-45ef-8222-cacc4c19994c" />
<img width="601" height="514" alt="image" src="https://github.com/user-attachments/assets/534ec70c-d5ae-4084-928a-262fc1f1ac54" />

    - 마크다운 형식으로 작성 및 확인 가능
    - 이슈 수정 페이지 이동 및 삭제 가능
    - 댓글 및 답글 작성 가능
    - 댓글 및 답글에 좋아요 가능
    - 이슈 태그 등록, 수정, 삭제 가능
    - 수정은 본인만, 삭제는 관리자 이상 멤버 혹은 본인만 가능
    - 할당자 선택 시 검색 및 무한스크롤 가능


#### - 프로젝트 깃허브 연동
  <img width="1901" height="888" alt="image" src="https://github.com/user-attachments/assets/1adcefa2-03e5-4dc7-bc02-20feb23baa0d" />
  <img width="1893" height="890" alt="image" src="https://github.com/user-attachments/assets/867128c6-b0f3-40ec-8c45-9db7296ae7e7" />
  <img width="945" height="633" alt="image" src="https://github.com/user-attachments/assets/7ea0e2ef-196a-4f75-bb91-0ff015f2a8dd" />


    - 프로젝트 깃허브 연동 기능을 이용하기 위해 계정을 깃허브와 연동 및 해제 가능
    - 프로젝트 깃허브 연동 기능을 이용하기 위해 우측 상단의 프로필 시트에 깃허브 토큰 관리 버튼으로 GitHub Personal Access Token 등록, 수정, 삭제 가능
    - 특정 프로젝트를 GitHub Personal Access Token으로 조회한 레포지토리 목록 중 하나와 연동 가능
    - 프로젝트-레포지토리 연동시 모든 마일스톤, 이슈, 이슈 댓글을 불러옴
    - 이후 연동된 상태에서 마일스톤, 이슈, 이슈 댓글을 등록, 수정, 삭제시 깃허브 레포지토리에 동일하게 반영
    - ex) 이슈 완료됨으로 상태 변경 -> 이슈 CLOSED / 이슈 삭제 -> 이슈 CLOSED + 제목변경 및 삭제 안내 댓글 추가
    - 동기화 버튼을 클릭 시 연동된 레포지토리의 최신 데이터를 불러옴
    - 연동해제시 기존 데이터는 남아있어 그대로 사용 가능

 ------

#### - 프로젝트 정보 페이지/프로젝트 수정 모달
<img width="1894" height="895" alt="image" src="https://github.com/user-attachments/assets/5ea04bb4-9b79-4f7d-a19d-34d8e687a433" />
<img width="823" height="861" alt="image" src="https://github.com/user-attachments/assets/09be4bd5-5a25-4809-a740-774751a3f800" />

    - 프로젝트 정보 수정 가능(이름, 설명, 기간, 상태, 구성원)
    - 프로젝트 구성원 이름으로 검색 가능
    - 체크박스 클릭 시 구성원 삭제 가능
    - 관리자 이상 경우 본인의 비밀번호 확인 후 프로젝트 삭제 가능

-----

#### - 게시판 목록 페이지/게시글 목록 페이지/게시글 상세 페이지
  <img width="1895" height="896" alt="image" src="https://github.com/user-attachments/assets/85ffa9c1-16f3-44bf-b055-9cca03ba61c4" />
  <img width="1898" height="891" alt="image" src="https://github.com/user-attachments/assets/b15b893a-315e-4261-8420-1d595f8274d7" />
  <img width="1898" height="893" alt="image" src="https://github.com/user-attachments/assets/8fc83123-159b-4e98-8755-0bd8c82863f9" />
  
    - 좌측 사이드바로 게시글 목록 이동 가능
    - 관리자만 게시판 추가 가능
    - 수정은 본인만, 삭제는 관리자 이상 멤버 혹은 본인만 가능

-----

#### - 채팅 목록 페이지/채팅방 페이지
  <img width="1897" height="890" alt="image" src="https://github.com/user-attachments/assets/05af7e62-6ece-485e-acc9-9bb9641218f0" />
  
  <img width="1897" height="893" alt="image" src="https://github.com/user-attachments/assets/faa27025-7031-48bd-8433-c4992248b46b" />
  <img width="596" height="751" alt="image" src="https://github.com/user-attachments/assets/389e7db6-feb0-44e6-be85-8b83762db472" />

  <img width="850" height="727" alt="image" src="https://github.com/user-attachments/assets/1b406f05-b72f-41c6-b908-0192eb81803f" />

    - 참여자 초대 안할 시 나만의 채팅방으로 생성 
    - 좌측 사이드바로 채팅방 이동 가능
    - 본인이 참여 중인 채팅방 조회 가능
    - 채팅방 위로 무한스크롤 가능
    - 현재 채팅방 참여자 제외 회사 구성원 초대 가능
    - 채팅방 이름 변경 가능
    - 채팅방 상세정보 확인 가능
    - 채팅방 나가기 가능
 

-----

#### - 실시간 알림창
<img width="344" height="899" alt="image" src="https://github.com/user-attachments/assets/ece815cb-e67b-45e9-acdc-c389a10addfc" />
<img width="350" height="889" alt="image" src="https://github.com/user-attachments/assets/9efa4a73-2281-42cc-9a5c-420824eb5185" />


    - 다섯가지 타입 알림(이슈 할당, 채팅, 댓글 답글, 내 이슈 댓글 및 답글, 내 게시글 댓글 및 답글)
    - 알림 모두 읽기 처리 가능
    - 알림 클릭시 읽음처리 후 해당 페이지로 이동
    - 무한스크롤 기능

-----

#### - AI 챗봇
<img width="1362" height="606" alt="image" src="https://github.com/user-attachments/assets/c3a215da-277d-44b7-b370-e7ad5e4f96a8" />
<img width="1363" height="617" alt="image" src="https://github.com/user-attachments/assets/3844972c-b058-4307-9fab-e305e1237f54" />

    - 채팅 요약, 프로젝트 및 구성원 관련 질문에 응답 가능
    - ex) 우리 회사의 부서 리스트는?
    - 내가 참여 중인 채팅방 목록 알려줘
    - ㅇㅇㅇ채팅방에서 대화했던 내용 요약해줘
    - ㅇㅇㅇ의 직책은 뭐야?
    - ㅇㅇㅇ프로젝트에서 내게 할당된 이슈는?



  

[⚙️ API 명세 (API Endpoints)](https://www.notion.so/REST-API-25e83bb40b0a80dba920f0b8869999ff?source=copy_link)
[📜 프로젝트 규칙 (Conventions)](https://www.notion.so/Convention-25e83bb40b0a808b933ddaca984109ab?source=copy_link)
-----

## 👨‍👩‍👧‍👦 팀원 (Team)

| 이름      | 역할 |                  GitHub                   |
|:--------| :---: |:-----------------------------------------:|
| `[김시은]` | `[PM]` |   [바로가기](https://github.com/SerahKim)   |
| `[류지원]` | `[DB관리자]` |   [바로가기](https://github.com/lalabong)   |
| `[이지은]` | `[형상관리자]` |    [바로가기](https://github.com/ddzeun)     |

## 담당 파트
<img width="1123" height="621" alt="image" src="https://github.com/user-attachments/assets/d0af785e-05e6-4879-a3bd-3b568e54ec60" />
<img width="1123" height="620" alt="image" src="https://github.com/user-attachments/assets/ad48855c-0364-4c11-aeea-e94f21638eac" />
<img width="1124" height="633" alt="image" src="https://github.com/user-attachments/assets/5078f172-bab1-4c90-a155-dec841d7ed42" />



