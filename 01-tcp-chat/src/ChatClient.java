import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        // 1. 서버에 접속 - localhost(내 컴퓨터)의 8080 포트로 TCP 연결을 요청한다
        // ServerSocket과 달리 Socket은 직접 상대방에게 접속하는 역할
        // 서버가 accept()하면 연결이 성립되고, 양방향 통신이 가능해진다
        Socket socket = new Socket("localhost", 8080);
        System.out.println("서버 접속 완료");

        // 2. 입력 스트림 - 서버가 보낸 데이터를 읽기 위한 통로
        // 서버와 동일한 구조: InputStream -> InputStreamReader -> BufferedReader
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        // 출력 스트림 - 서버에게 데이터를 보내기 위한 통로
        PrintWriter out = new PrintWriter(
          socket.getOutputStream(),true
        );

        // 콘솔 입력
        Scanner scanner = new Scanner(System.in);

        // 3. 수신 전용 스레드 - 서버로부터 오는 메시지를 별도 스레드에서 수신한다
        // 서버와 동일한 이유: 메인 스레드가 송신을 담당하므로 수신을 분리해야
        //                  입력 중에도 메시지를 받을 수 있다
        Thread receiveThread = new Thread(() -> {
           try {
               String message;
               while ((message = in.readLine()) != null) {
                   System.out.println("서버: " + message);
               }
           } catch (IOException e) {
               System.out.println("연결이 종료되었습니다.");
           }
        });
        receiveThread.start();

        // 4. 메시지 송신 루프 - 키보드로 입력한 메시지를 서버에게 전송한다
        while (true) {
            String myMessage = scanner.nextLine();
            if ("/quit".equals(myMessage)) break;
            out.println(myMessage);
        }

        // 5. 자원 해제 - 소켓을 닫아 연결을 종료하고 자원을 반환한다
        // 클라이언트는 ServerSocket이 없으므로 Socket만 닫으면 된다
        socket.close();
        System.out.println("접속 종료");
    }
}