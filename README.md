# Java Socket Study
Socket 통신의 기본 원리를 학습하고 직접 구현하며 이해하기 위한 프로젝트입니다.

## 학습 동기
배치 시스템과 API 연동 서버를 개발하면서 서버 간 통신에 대해 관심이 생겼고
TCP Socket 수준에서의 통신 원리를 직접 구현하며 이해하고자 시작했습니다.

## 프로젝트 목록
| # | 프로젝트 | 설명 | 상태 |
|---|--------|-----|-----|
| 01 | TCP 1:1 채팅 | ServerSocket/Socket 기반 양방향 통신 | 구현 |
| 02 | 간이 HTTP 서버 | Socket으로 HTTP 요청 파싱 및 응답 | 구현 |
| 03 | TCP 중계 채팅 | 서버가 중계자 역할로 다수 클라이언트 간 메시지 전달 (Broadcast) | 구현 중 |

## 기술 스택
- Java 17
- TCP/IP Socket (java.net)
- Multi-Thread (java.lang.Thread) 
