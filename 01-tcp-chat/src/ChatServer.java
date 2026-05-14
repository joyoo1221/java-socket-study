import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer {
    public static void main(String[] args) throws IOException {
        /**
         * 1. 서버 소켓 생성 - 8080 포트에서 클라이언트 접속을 대기한다
         * ServerSocket은 직접 통신하지 않고, 접속 요청을 받아 Socket을 생성하는 역할
         */
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("서버 시작. 클라이언트 접속 대기 중.");

        /**
         * 2. 클라이언트 접속 대기 - 접속이 올 때까지 이 줄에서 멈춰있다가 클라이언트가 접속하면 실제 통신에 사용할 Socket 객체를 반환한다
         */
        Socket socket = serverSocket.accept();
        System.out.println("클라이언트 접속");

        /**
         * 3. 입력 스트림 - 클라이언트가 보낸 데이터를 읽기 위한 통로
         * InputStream(바이트) → InputStreamReader(문자 변환) → BufferedReader(한 줄 단위 읽기)
         */
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        ); // 상대방 메시지 읽기

        /**
         * 출력 스트림 - 클라이언트에게 데이터를 보내기 위한 통로
         */
        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true  // true: autoFlush 설정. println() 호출 시 즉시 전송한다
        ); // 상대방에게 메시지 보내기

        /**
         * 콘솔 입력 - 서버 사용자가 키보드로 메시지를 입력받기 위한 용도
         */
        Scanner scanner = new Scanner(System.in);

        /**
         * 4. 수신 전용 스레드 - 메시지를 받는 작업을 별도 스레드로 분리한다
         * 이유: 메인 스레드가 키보드 입력(송신)을 담당하고 있으므로 수신을 같은 스레드에서 하면 입력 중에 메시지를 받을 수 없다
         */
        Thread receiveThread = new Thread(() -> {
           try {
               String message;
               // readLine(): 데이터가 올 때까지 대기, 상대방이 연결을 끊으면 null을 반환
               while ((message = in.readLine()) != null) {
                   System.out.println("클라이언트: " + message);
               }
           } catch (IOException e) {
               System.out.println("연결이 종료되었습니다.");
           }
        });
        receiveThread.start();

        /**
         * 5. 메시지 송신 루프 - 키보드로 입력한 메시지를 클라이언트에게 전송한다
         */
        while (true) {
            String myMessage = scanner.nextLine();
            // "/quit" 입력 시 루프를 빠져나와 연결을 종료
            if ("/quit".equals(myMessage)) break;
            out.println(myMessage);
        }

        /**
         * 6. 자원 해제 - 사용한 소켓과 서버 소켓을 닫아 포트와 메모리를 반환한다
         */
        socket.close();
        serverSocket.close();
        System.out.println("서버 종료");
    }
}