# Blog Server API

## 📁 프로젝트 구조

```
blog-server/
├── api/          # REST API 모듈 (Spring Boot 메인)
├── board/        # 게시판 도메인 모듈 
├── login/        # GitHub OAuth2 인증 모듈
└── buildSrc/     # Gradle 공통 설정
```

## 🛠 기술 스택

- **Spring Boot 3.4.0** + Spring Security OAuth2
- **MySQL 8.4.5** + JPA/Hibernate  
- **GitHub OAuth2** 인증
- **Clean Architecture** + Multi-Module

## 🔐 인증

### GitHub OAuth2
- **관리자**: `NohYeongO` (GitHub ID)
- **로그인**: `/oauth2/authorization/github`
- **권한**: 관리자만 CUD 작업 가능

## 📋 API 상세 스펙

### 🔑 인증 API

#### 1. 현재 사용자 정보 조회
```http
GET /api/auth/user
Authorization: Required (OAuth2 Session)
```

**Response (200 OK):**
```json
{
  "authenticated": true,
  "githubId": "NohYeongO",
  "name": "노영오",
  "role": "ADMIN"
}
```

#### 2. 로그인 오류 정보
```http
GET /api/auth/login-error?reason=not_admin
```

**Response (401 Unauthorized):**
```json
{
  "authenticated": false,
  "githubId": null,
  "name": null,
  "role": null,
  "message": "관리자 권한이 없습니다. 관리자만 로그인할 수 있습니다."
}
```

### 📂 카테고리 API

#### 1. 카테고리 목록 조회
```http
GET /api/categories
Authorization: Not Required (Public)
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "개발 일지"
  },
  {
    "id": 2,
    "name": "기술 블로그"
  }
]
```

#### 2. 카테고리 생성
```http
POST /api/categories
Authorization: Required (Admin Only)
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "새로운 카테고리"
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "name": "새로운 카테고리"
}
```

**Error Response (409 Conflict):**
```json
{
  "message": "이미 존재하는 카테고리입니다.",
  "timestamp": "2024-12-24T10:00:00"
}
```

#### 3. 카테고리 수정
```http
PUT /api/categories/{categoryId}
Authorization: Required (Admin Only)
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "수정된 카테고리명"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "수정된 카테고리명"
}
```

#### 4. 카테고리 삭제
```http
DELETE /api/categories/{categoryId}
Authorization: Required (Admin Only)
```

**Response (204 No Content):**
```
(Empty Body)
```

**Error Response (404 Not Found):**
```json
{
  "message": "카테고리를 찾을 수 없습니다.",
  "timestamp": "2024-12-24T10:00:00"
}
```

### 📝 게시글 API

#### 1. 게시글 목록 조회 (페이징)
```http
GET /api/posts?categoryName={category}&page={page}&size={size}&sort={sort}
Authorization: Not Required (Public - 발행된 글만 조회)
Authorization: Required (Admin - 모든 글 조회)
```

**Query Parameters:**
- `categoryName` (optional): 카테고리 이름으로 필터링
- `page` (optional, default=0): 페이지 번호
- `size` (optional, default=10): 페이지 크기
- `sort` (optional): 정렬 기준 (예: "createdDate,desc")

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Spring Boot 블로그 개발기",
      "author": "NohYeongO",
      "published": true,
      "category": {
        "id": 1,
        "name": "개발 일지"
      },
      "createdDate": "2024-12-24T10:00:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "currentPage": 0,
  "size": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

#### 2. 특정 게시글 조회
```http
GET /api/posts/{postId}
Authorization: Not Required (Public)
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Spring Boot 블로그 개발기",
  "content": "Spring Boot를 사용하여 개인 블로그를 개발한 경험을 공유합니다...",
  "author": "NohYeongO",
  "published": true,
  "category": {
    "id": 1,
    "name": "개발 일지"
  },
  "createdDate": "2024-12-24T10:00:00",
  "modifiedDate": "2024-12-24T15:30:00"
}
```

#### 3. 게시글 생성
```http
POST /api/posts
Authorization: Required (Admin Only)
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "새 게시글 제목",
  "content": "게시글 내용입니다...",
  "categoryName": "개발 일지",
  "published": true
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "title": "새 게시글 제목",
  "content": "게시글 내용입니다...",
  "author": "NohYeongO",
  "published": true,
  "category": {
    "id": 1,
    "name": "개발 일지"
  },
  "createdDate": "2024-12-24T16:00:00",
  "modifiedDate": "2024-12-24T16:00:00"
}
```

#### 4. 게시글 수정
```http
PUT /api/posts/{postId}
Authorization: Required (Admin Only)
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "수정된 게시글 제목",
  "content": "수정된 게시글 내용입니다...",
  "categoryName": "기술 블로그",
  "published": false
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "수정된 게시글 제목",
  "content": "수정된 게시글 내용입니다...",
  "author": "NohYeongO",
  "published": false,
  "category": {
    "id": 2,
    "name": "기술 블로그"
  },
  "createdDate": "2024-12-24T10:00:00",
  "modifiedDate": "2024-12-24T17:00:00"
}
```

#### 5. 게시글 삭제
```http
DELETE /api/posts/{postId}
Authorization: Required (Admin Only)
```

**Response (204 No Content):**
```
(Empty Body)
```

## 🚨 공통 오류 응답

### 401 Unauthorized (인증 실패)
```json
{
  "message": "인증이 필요합니다.",
  "timestamp": "2024-12-24T10:00:00"
}
```

### 403 Forbidden (권한 부족)
```json
{
  "message": "관리자 권한이 필요합니다.",
  "timestamp": "2024-12-24T10:00:00"
}
```

### 404 Not Found (리소스 없음)
```json
{
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "timestamp": "2024-12-24T10:00:00"
}
```

### 400 Bad Request (유효성 검증 실패)
```json
{
  "message": "제목은 필수 항목입니다.",
  "timestamp": "2024-12-24T10:00:00"
}
```

## 🚀 실행 방법

```bash
# 서버 실행
./gradlew :api:bootRun

# 접속
http://localhost:8080

# GitHub OAuth2 로그인
http://localhost:8080/oauth2/authorization/github
```
