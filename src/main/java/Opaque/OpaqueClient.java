package Opaque;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OpaqueClient {
    private static final String serverAddress = "localhost"; // 服务器地址
    private static final int serverPort = 9000; // 服务器端口号

    public static void main(String[] args) throws Exception {
        // 用户名和密码将通过FIDO认证而改变
        // 通过使用方法getUserInfo()获取User类user对象的用户名和密码
        // OpaqueUser user = getUserInfo();
        String username = "liu"; // 用户名
        String password = "yaowen"; // 密码

        // 创建Socket对象，并连接到服务器
        Socket socket = new Socket(serverAddress, serverPort);
        if (socket.getRemoteSocketAddress() == null) {
            System.out.println("连接错误");
        }
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 发送用户名
        out.println(username);

        // 接收服务器发送的盐值
        String salt = in.readLine();

        // 计算客户端的OPAQUE凭证
        // TODO: OPAQUE协议的实现
        // String credential = OpaqueProtocol.calculateCredential(username, password, salt);
        String credential = "yes!!!";

        // 发送客户端的凭证
        out.println(credential);

        // 接收服务器发送的认证结果
        String result = in.readLine();
        if ("success".equals(result)) {
            System.out.println("Authentication success!");
        } else {
            System.out.println("Authentication failed!");
        }

        // 关闭连接
        socket.close();
    }
}
