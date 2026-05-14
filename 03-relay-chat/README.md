## 03. TCP 중계 채팅

### 목표


### 1. 왜 이 프로젝트를 했는가?
1:1 채팅을 넘어서 1:다 구도로 차근차근 넘어가면서 구조를 익히고 싶었다.

### 2. 구조를 어떻게 설계했는가?
#### 2.1.1.
```mermaid
graph LR
A["<b>[클라이언트 A]</b><br/>메시지 입력 및 송신"]
S["<b>[서버 = 중계자]</b><br/>받은 메시지를 그대로 전달"]
B["<b>[클라이언트 B]</b><br/>전달받은 메시지 출력"]

    A <-- "[닉네임] 안녕하세요" --> S
    S <-- "[닉네임] 안녕하세요" --> B
```

#### 2.1.2. 01-tcp-chat과의 차이점
|   | 01-tcp-chat | 03-relay-chat |
|---|-------------|---------------|
| 서버역할 | 직접 대화에 참여   | 메시지 전달만       |
| 클라이언트 수 | 1명          | 2명 이상         |
| Thread | 수신용 1개      | 클라이언트마다 1개씩   |
| Broadcast | x           | o             |

#### 2.2. 요청-응답 흐름


#### 2.3. HTTP 응답 구조


#### 2.4. 경로별 분기 처리


### 3. 실행 방법
1. RelayServer 실행
2. RelayChatClient 실행 (1번째) → 닉네임 입력
3. RelayChatClient 추가 실행 (2번째) → 닉네임 입력
4. 한쪽에서 메시지 입력 → 다른 쪽에 출력

>RelayChatClient 다중으로 실행하는 방법 (IntelliJ 기준)
>  1. ChatClient.java 파일 열기
>  2. 상단 메뉴 Run → Edit Configurations
>  3. 왼쪽에서 ChatClient 선택
>  4. Modify options 클릭 → Allow multiple instances 체크
>  5. Apply → OK
>  6. ChatClient를 여러 번 실행하면 하단에 탭이 여러 개 생기는 것을 확인 할 수 있다.

### 4. 실행 화면
#### 4.1. RelayServer 실행
![](/03-relay-chat/images/RelayServer01.png)</br>
#### 4.2. RelayChatClient 실행 (1번째)
| | RelayServer | RelayChatClient |
|----------------------|--------------------|---|
|RelayChatClient 실행 시|![](/03-relay-chat/images/RelayServer02.png)|![](/03-relay-chat/images/RelayChatClient01.png)|
|닉네임 입력 후 메시지 입력|![](/03-relay-chat/images/RelayServer03.png)|![](/03-relay-chat/images/RelayChatClient02.png)|
#### 4.3. RelayChatClient 추가 실행 (2번째)
|                         | RelayServer | RelayChatClient |
|-------------------------|--------------------|-|
| RelayChatClient 추가 실행 시 |![](/03-relay-chat/images/RelayServer04.png)||
| 닉네임 입력 후 메시지 입력         |![](/03-relay-chat/images/RelayServer05.png)|![](/03-relay-chat/images/RelayChatClient03.png)|


### 5. 어떤 문제를 만났고 어떻게 해결했는가?
#### 5.1. java.net.BindException: Address already in use 에러
| 구분 | 설명                                                                                                                                                  |
|----|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| 상황 | 재테스트를 위해 RelayServer를 종료 후 다시 켰는데, 에러메시지가 뜨며 제대로 켜지지 않았다.                                                                                           |
| 원인 | RelayServer가 사용하려는 8080 포트를 이미 다른 프로세스(또는 이전에 실행했던 서버)가 점유하고 있어서 발생하는 문제.</br>서버를 종료했더라도 프로세스가 백그라운드에 남아있거나, 운영체제에서 포트를 완전히 해제하지 않았을 때 자주 발생한다고 한다. |
| 해결 | 점유 중인 포트 강제 종료하기.<br>```1) lsof -i :8080 2) PID 번호 확인 3) kill -9 [port]```                                                                          |

### 6. 배운 점
- 

