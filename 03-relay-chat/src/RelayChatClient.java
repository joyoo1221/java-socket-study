import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class RelayChatClient {
    public static void main(String[] args) throws IOException {
        /**
         * 1. 서버에 접속 - 01과 동일
         */
        Socket socket = new Socket("localhost", 8080);
        System.out.println("서버 접속 완료");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true
        );
        Scanner scanner = new Scanner(System.in);

        /**
         * 2. 닉네임 입력 - 01에서는 없던 기능
         * 여러명이 채팅하므로 누가 보낸 메시지인지 구분해야 한다
         */
        System.out.println("닉네임 입력: ");
        String nickname = scanner.nextLine();

        /**
         * 3. 수신 전용 스레드 - 01과 동일한 구조
         */
        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("연결이 종료되었습니다.");
            }
        });
        receiveThread.start();

        /**
         * 4. 메시지 송신 - 닉네임을 붙여서 전송
         * 01에서는 그냥 텍스트만 보냈지만, 03에서는 "[닉네임] 메시지" 형태로 보내서 누가 말했는지 표시
         */
        while (true) {
            String myMessage = scanner.nextLine();
            if ("/quit".equals(myMessage)) break;
            out.println("[" + nickname + "] " + myMessage);
        }

        /**
         * 5. 자원 해제
         */
        socket.close();
        System.out.println("접속 종료");
    }
}
