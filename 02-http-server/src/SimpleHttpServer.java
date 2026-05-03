import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        // 1. 서버 소켓 생성 - 8080 포트에서 HTTP 요청을 대기
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("HTTP 서버 시작: http://localhost:8080");

        // 2. 클라이언트 요청을 반복적으로 처리 (while문으로 여러 요청 수신)
        while (true) {
            // 브라우저가 접속하면 Socket 생성
            Socket socket = serverSocket.accept();

            // 3. 요청 읽기 - 브라우저가 보낸 HTTP 요청 메시지를 한 줄 씩 읽는다
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // 첫 번째 줄이 요청 라인 (ex: "GET /hello HTTP/1.1")
            String requestLine = in.readLine();
            System.out.println("요청: " + requestLine);

            // 4. 요청 경로 추출 - "GET /hello HTTP/1.1"에서 "/hello" 부분만 꺼낸다
            String path = "/";
            if (requestLine != null && requestLine.startsWith("GET")) {
                path = requestLine.split(" ")[1];   // 공백으로 나눠서 두 번째 값
            }

            // 5. 경로에 따라 응답 본문 생성
            String body;
            if ("/hello".equals(path)) {
                body = "<h1>Hello, World!</h1>";
            } else if ("/time".equals(path)) {
                body = "<h1>현재 시간: " + java.time.LocalDateTime.now() + "</h1>";
            } else {
                body = "<h1>Java Socket HTTP Server</h1>" +
                        "<p>사용 가능한 경로: /hello, /time</p>";
            }

            // 6. HTTP 응답 메시지 작성 - HTTP 규격에 맞는 형식으로 응답
            // 응답 라인 + 헤더 + 구분선 + 본문
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println("Content-Length: " + body.getBytes("UTF-8").length);
            out.println();
            out.println(body);

            // 7. 자원 해제 - 응답 후 연결 종료 (HTTP는 요청-응답 후 끊는 게 기본)
            socket.close();
        }
    }
}