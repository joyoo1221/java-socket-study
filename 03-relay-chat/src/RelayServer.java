import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RelayServer {
    /**
     * 접속한 모든 클라이언트의 출력 스트림을 저장하는 리스트
     * 01에서는 클라리언트가 1명이라 변수 하나로 충분했지만, 여러명을 관리해야 하므로 List로 저장한다
     */
    private static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        /**
         * 1. 서버 소켓 생성 - 01과 동일
         */
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("중계 서버 시작. 클라이언트 접속 대기 중.");

        /**
         * 2. 여러 클라이언트를 계속 받는 루프
         * 01에서는 accept() 한 번만 호출했지만 (1명만 받으니까),
         * 여기서는 while(true)로 계속 새 클라이언트를 받는다
         */
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("새 클라이언트 접속. 현재 " + (clients.size() + 1) + "명");

            /**
             * 3. 이 클라이언트의 출력 스트림을 리스트에 추가
             * 나중에 broadcast할 때 이 리스트를 순회하며 모두에게 메시지를 보낸다
             */
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            clients.add(out);

            /**
             * 4. 클라이언트마다 전용 Thread 생성
             * 01에서는 서버가 직접 대화했지만 (Scanner + 수신 Thread), 여기서는 서버가 대화에 참여하지 않고 중계만 한다.
             * 각 클라이언트의 메시지를 듣기 위헤 Thread를 1개씩 배정
             */
            Thread clientThread = new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    );

                    String message;
                    /**
                     * 5. 클라이언트가 보낸 메시지를 읽고, 다른 모든 클라이언트에게 전달
                     */
                    while ((message = in.readLine()) != null) {
                        System.out.println("수신: " + message);
                        broadcast(message, out);    // broadcast - 자신을 제외한 모든 클라이언트에게 메시지 전달
                    }
                } catch (IOException e) {
                    System.out.println("클라이언트 연결 종료");
                } finally {
                    /**
                     * 6. 연결이 끊기면 리스트에서 제거
                     */
                    clients.remove(out);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("클라이언트 퇴장. 현재 " + clients.size() + "명");
                }
            });
            clientThread.start();
        }
    }

    /**
     * broadcast 메서드 - 보낸 사람을 제외한 모든 클라이언트에게(=리스트에 있는 클라이언트 전부에게) 메시지 전달
     * 01에서는 이 개념이 없었다 (상내가 1명 뿐이기 때문)
     * 카톡 단체방에서 한 명이 메시지 치면 나머지 전원에게 전송되는 것과 같다
     */
    private static void broadcast(String message, PrintWriter sender) {
        for (PrintWriter client : clients) {
            if (client != sender) { // 보낸 사람에게 다시 보내지 않는다
                client.println(message);
            }
        }
    }
}
