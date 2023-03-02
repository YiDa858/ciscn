package OPAQUE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class OpaqueServer {
    private static final int serverPort = 9000; // 服务器端口号
    private static final int timeout = 30;

    public static void main(String[] args) throws Exception {
        // 创建ServerSocket对象，并监听端口号
        ServerSocket serverSocket = new ServerSocket(serverPort);
        serverSocket.setSoTimeout(timeout);

        while (true) {
            // 接收客户端的连接请求，并创建Socket对象
            Socket socket = serverSocket.accept();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 接收客户端发送的用户名
            String username = in.readLine();

            // 生成盐值，并发送给客户端
            // TODO: OPAQUE协议的实现
            // String salt = OpaqueProtocol.generateSalt();
            String salt = "salt!!!";
            out.println(salt);

            // 接收客户端发送的凭证，验证身份
            String credential = in.readLine();

            // TODO: OPAQUE协议的实现
            // OpaqueProtocol.authenticate(username, credential)

            // 关闭连接
            socket.close();
        }
    }
}
