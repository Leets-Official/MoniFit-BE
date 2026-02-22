# MoniFit-BE

> **"건강한 소비습관, 모니핏과 함께💸."**
> 사용자 중심의 예산 관리 및 지출 분석 기능을 제공하는 RESTful API 서버입니다.

---

## 💎 Project Highlights

* **정교한 예산 주기 관리**: 사용자가 설정한 기간별 예산(Active/Completed)을 추적하고, 소비 현황을 실시간으로 계산하는 도메인 로직을 구현했습니다.
* **데이터 기반 소비 인사이트**: 일일 지출 데이터와 예산을 결합하여 대시보드 통계 및 캘린더 기반 시각화 데이터를 제공합니다.
* **안전한 인증 및 소셜 연동**: Kakao OAuth 2.0과 JWT(Access/Refresh Token)를 활용한 보안 시스템을 구축하고, Refresh Token을 DB에 관리하여 세션 안정성을 높였습니다.
* **확장성 있는 아키텍처**: 비즈니스 로직의 응집도를 높인 레이어드 아키텍처를 채택하여 유지보수와 기능 확장에 용이하도록 설계했습니다.
* **표준화된 응답 체계**: `ApiResponse` 공통 포맷과 글로벌 예외 처리를 통해 프론트엔드와의 협업 효율을 극대화했습니다.

---

## ✨ Key Features

### 📅 지출 및 예산 관리 시스템
* **예산 주기 설계**: 특정 기간을 설정하여 지출을 관리하며, 목표 대비 소비율을 계산하여 피드백을 제공합니다.
* **카테고리별 지출**: 식비, 카페, 교통 등 카테고리별 지출 기록 및 수정 기능을 제공합니다.
* **스탬프 시스템**: 일일 소비 목표 달성 여부에 따라 스탬프를 부여하여 사용자의 동기를 부여합니다.

### 📊 분석 대시보드 API
* **실시간 통계**: 현재 진행 중인 주기의 총 지출액, 잔여 예산, 카테고리별 소비 분포 데이터를 제공합니다.
* **캘린더 뷰**: 날짜별 지출 내역을 한눈에 파악할 수 있는 시각화 데이터를 전송합니다.

### 🔐 인증 및 사용자 관리
* **Kakao Login**: 소셜 로그인을 통한 간편한 진입점과 회원 정보 관리 기능을 구현했습니다.
* **토큰 매니지먼트**: 만료된 Access Token을 Refresh Token으로 재발급하는 보안 워크플로우를 포함합니다.

---

## 🛠 Tech Stack

### Framework & Language
![Java](https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

### Data & Database
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

### Infrastructure & Tools
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

---

## 👥 Team

| 성명 | 역할 |
| :--- | :--- |
| **성현준** | **Backend (Part Lead)** |
| **박승주** | Backend |
| **강태이** | Backend |

---

## 📂 System Architecture

```mermaid
graph TD
    subgraph Client
        A[Mobile Web/App]
    end

    subgraph Server
        B[Spring Boot Application]
        C[Spring Security / JWT Filter]
    end

    subgraph External
        D[Kakao OAuth API]
    end

    subgraph Database
        E[(MySQL / MariaDB)]
    end

    A <-->|REST API / JWT| C
    C <--> B
    B <-->|JPA| E
    B <-->|OAuth 2.0| D
