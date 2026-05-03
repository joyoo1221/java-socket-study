import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        // 1. 서버 소켓 생성 - 8080 포트에서 HTTP 요청을 대기
        // 01-tcp-chat의 ChatServer와 동일하게 ServerSocket으로 포트를 열고 접속을 받는다
        // 차이점: 채팅은 한 명의 클라이언트와 연결하지만, HTTP 서버는 여러 요청을 반복 처리한다
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("HTTP 서버 시작: http://localhost:8080");

        // 2. 클라이언트 요청을 반복적으로 처리
        // while(true)로 서버가 종료될 때까지 계속 요청을 받는다
        // HTTP는 요청-응답 후 연결을 끊기 때문에, 매 요청마다 새로운 Socket이 생성된다
        // 이 때문에 ServerSocket.close()는 호출되지 않는다 (루프가 끝나지 않으므로)
        while (true) {
            // 브라우저가 접속하면 Socket 생성
            // 01-tcp-chat과 달리 별도의 ChatClient가 필요 없다 - 브라우저가 클라이언트 역할을 한다
            Socket socket = serverSocket.accept();

            // 3. 요청 읽기 - 브라우저가 보낸 HTTP 요청 메시지를 한 줄 씩 읽는다
            // 01-tcp-chat에서는 자유 형식의 텍스트를 주고받았지만,
            // HTTP는 "GET /hello HTTP/1.1" 같은 정해진 규격을 따른다
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // HTTP 요청의 첫 번째 줄 = 요청 라인
            // 형식: [메서드] [경로] [HTTP버전]  예: "GET /hello HTTP/1.1"
            String requestLine = in.readLine();
            System.out.println("요청: " + requestLine);

            // 4. 요청 경로 추출 - "GET /hello HTTP/1.1"에서 "/hello" 부분만 꺼낸다
            // "GET /hello HTTP/1.1"을 공백으로 나누면 ["GET", "/hello", "HTTP/1.1"]
            // 두 번째 값(인덱스 1)이 경로(/hello)이다
            String path = "/";
            if (requestLine != null && requestLine.startsWith("GET")) {
                path = requestLine.split(" ")[1];   // 공백으로 나눠서 두 번째 값
            }

            // 5. 경로에 따라 응답 본문 생성
            // Spring의 @GetMapping처럼 경로별로 다른 응답을 반환하는 구조
            // Spring이 내부적으로 해주는 라우팅을 여기서는 직접 if문으로 처리한다
            String body;
            if ("/hello".equals(path)) {
                body = "<h1>Hello, World!</h1>";
            } else if ("/time".equals(path)) {
                body = "<h1>현재 시간: "
                        + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + "</h1>";
            } else {
                body = "<h1>Java Socket HTTP Server</h1>"
                        + "<p>사용 가능한 경로: /hello, /time</p>";
            }

            // 6. HTTP 응답 메시지 작성 - HTTP 규격에 맞는 형식으로 응답
            // HTTP 응답은 반드시 아래 구조를 따라야 브라우저가 해석할 수 있다
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HTTP/1.1 200 OK"); // 상태 라인: HTTP/1.1 200 OK (200 = 정상 처리)
            out.println("Content-Type: text/html; charset=UTF-8");  // 헤더: Content-Type (본문 형식), Content-Length (본문 크기)
            out.println("Content-Length: " + body.getBytes("UTF-8").length);
            out.println();  // 빈 줄: 헤더와 본문의 구분선. 이 줄이 없으면 브라우저가 본문을 인식 못한다
            out.println(body);

            // 7. 자원 해제 - 응답 후 연결 종료 (HTTP는 요청-응답 후 끊는 게 기본)
            // HTTP는 요청-응답이 끝나면 연결을 끊는 것이 기본이다 (비연결성)
            // 01-tcp-chat은 연결을 유지하며 계속 대화했지만, HTTP는 매번 끊고 다시 연결한다
            socket.close();
        }
    }
}