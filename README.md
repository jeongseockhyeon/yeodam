# Yeodam

## 기술스택
------
### 백엔드

<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src = "https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">


### 프론트엔드

<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/JSS-F7DF1E?style=for-the-badge&logo=JSS&logoColor=white">
<img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white">
<img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">

## ERD
------

![image](/uploads/10bf0caa122776e65be40905430b2a8a/image.png)


## 와이어프레임
------

![image](/uploads/0a03ecb6d2851c5b50dd509cddd98816/image.png)


## 주요 기능

### 회원

### 판매자

### 상품

+ 여행 상품 등록
    + 이미지를 포함한 상품 등록 시 커스텀 어노테이션을 통한 확장자, 파일 이름 내 특수문자 검사

+ 여행 상품 조회
    + 기본 생성순 조회

    + 가격 오름차순/내림차순 조회

    + 평점 오름차순/내림차순 조회
    
    + 지역/기간/테마 별 조건 조회

+ 여행 상품 수정

+ 여행 상품 삭제

### 장바구니
+ 비회원 - 로컬스토리지 장바구니

+ 회원 - db 장바구니

로그인 시 로컬스토리지 장바구니 상품이 회원 db 장바구니 상품과 병합

+ 장바구니 상품 추가
    + 가이드 이름과 예약일 선택 시 반영되어 장바구니에 등록

+ 장바구니 상품 전체/개별 선택 조회
    + 선택 상품 금액 계산

+ 장바구니 상품 전체/개별  삭제

### 주문
