## 03. TCP 중계 채팅

### 목표
서버가 중계자 역할을 하여, 여러 클라이언트가 한 명이 보낸 메시지를 동시에 주고받는 1:다 채팅 구현


### 1. 왜 이 프로젝트를 했는가?
1:1 채팅을 넘어서 1:다 구도로 차근차근 넘어가면서 구조를 익히고 싶었다.</br>
01-tcp-chat에서는 서버가 직접 대화에 참여했지만, 실제 채팅 서비스(카카오톡 단체방 등)는 서버가 대화에 끼지 않고 메시지를 받아서 다른 사람들에게 전달만 한다.</br>
서버를 중계자로 두고 여러 클라이언트를 동시에 관리하는 구조를 직접 구현하며 이해하고 싶어서 해당 프로젝트를 시작했다.

### 2. 구조를 어떻게 설계했는가?
#### 2.1. 전체 구조
```mermaid
graph LR
A["<b>[클라이언트 A]</b><br/>메시지 입력 및 송신"]
S["<b>[서버 = 중계자]</b><br/>받은 메시지를 그대로 전달"]
B["<b>[클라이언트 B]</b><br/>전달받은 메시지 출력"]

    A <-- "[닉네임] 안녕하세요" --> S
    S <-- "[닉네임] 안녕하세요" --> B
```

#### 2.2. 01-tcp-chat과의 차이점
|           | 01-tcp-chat | 03-relay-chat |
|-----------|-------------|---------------|
| 서버역할      | 직접 대화에 참여   | 메시지 전달만       |
| 클라이언트 수   | 1명          | 2명 이상         |
| 클라이언트 관리  | 변수 1개       | List로 여러 명 관리 |
| Thread    | 수신용 1개      | 클라이언트마다 1개씩   |
| Broadcast | x           | o             |

#### 2.3. 메시지 중계 흐름
01-tcp-chat은 한 번 연결 후 양쪽이 직접 대화하는 구조였지만</br>
03-relay-chat은 서버가 가운데에서 메시지를 받아 다른 클라이언트에게 다시 뿌려주는 구조다.
```mermaid
sequenceDiagram
    participant A as 클라이언트 A
    participant S as RelayServer
    participant B as 클라이언트 B

    S->>S: ServerSocket(8080) 대기
    A->>S: 접속 요청
    S->>S: accept() → clients 리스트에 A 추가, A 전용 Thread 생성
    B->>S: 접속 요청
    S->>S: accept() → clients 리스트에 B 추가, B 전용 Thread 생성

    Note over A,B: 연결 성립 - 중계 시작

    A->>S: "[닉네임] 안녕하세요" 송신
    S->>S: broadcast() - 보낸 사람(A) 제외
    S->>B: "[닉네임] 안녕하세요" 전달

    Note over A,B: /quit 입력 또는 연결 종료 시

    A->>S: 연결 종료
    S->>S: clients 리스트에서 A 제거, socket.close()
```

#### 2.4. broadcast 설계



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
- 서버가 직접 대화에 참여하지 않고, 메시지를 받아 다른 클라이언트에게 전달만 하는 중계자 역할을 할 수 있다는 것.
- 여러 클라이언트를 관리하려면 출력 스트림을 List에 모아두고 while(true) + accept()로 접속을 계속 받아야 한다.
- 클라이언트마다 Thread를 1개씩 배정해야 여러 명의 메시지를 동시에 받을 수 있다.
- broadcast는 보낸 사람을 제외하고 전달해야한다. 연결이 끊긴 클라이언트는 List에서 제거해 자원을 정리해야 한다.
- 1:다 구조에서는 메시지 보낸 사람을 구별하기 위해 닉네임과 같은 식별 정보가 필요하다.

