package Opaque;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class OpaqueClient {
    private Socket socket;
    private String username;
    private String password;
    private byte[] salt;
    private byte[] clientNonce;
    private byte[] serverNonce;
    private byte[] proof;
    private byte[] clientKey;
    private byte[] serverKey;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    private static final int SALT_LENGTH = 32;
    private static final int KEY_LENGTH = 32;

    public OpaqueClient(String username, String password) throws IOException {
        // 在创建Client对象时创建socket进行通讯
        this.socket = new Socket(SERVER_HOST, SERVER_PORT);
        this.username = username;
        this.password = password;
    }

    public void startRegister() throws NoSuchAlgorithmException, IOException {
        // 生成随机salt
        this.salt = generateSalt();

        // 使用SHA-256计算经过salt加盐后的密码散列
        byte[] hashedPassword = hashPassword(this.password, this.salt);

        // 将salt和密码散列发送给Server
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(this.salt.length);
        out.write(this.salt);
        out.writeInt(hashedPassword.length);
        out.write(hashedPassword);

        out.close();
    }

    public void startLogin() throws NoSuchAlgorithmException, IOException {
        DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
        DataInputStream in = new DataInputStream(this.socket.getInputStream());

        // 生成随机的clientNonce，并将其发送给服务器
        this.clientNonce = generateNonce();
        out.writeInt(this.clientNonce.length);
        out.write(this.clientNonce);

        // 从服务器获取serverNonce
        int serverNonceLength = in.readInt();
        this.serverNonce = new byte[serverNonceLength];
        in.readFully(serverNonce);

        // 使用clientNonce、serverNonce以及salt、用户名、密码作为参数计算出proof，以验证客户端身份
        this.proof = generateProof(this.username, this.password, this.salt, clientNonce, serverNonce);
        out.writeInt(this.proof.length);
        out.write(this.proof);

        out.close();
        in.close();
    }

    public void startKeyExchange() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
        DataInputStream in = new DataInputStream(this.socket.getInputStream());

        // 使用clientNonce和serverNonce、用户名和密码计算出客户端共享密钥
        this.clientKey = generateKey(this.username, this.password, this.salt, this.clientNonce, this.serverNonce);

        // 使用客户端共享密钥对消息进行加密，将密文发送给server
        byte[] encryptedMessage = encrypt(clientKey, "Hello, server!");
        out.writeInt(encryptedMessage.length);
        out.write(encryptedMessage);

        // 从服务器获取使用客户端共享密钥加密过的服务器端共享密钥
        byte[] encryptedKey = new byte[KEY_LENGTH];
        in.readFully(encryptedKey);
        this.serverKey = decrypt(this.clientKey, encryptedKey);

        // 使用服务器的共享密钥解密服务器发送的消息
        byte[] decryptedMessage = decrypt(this.serverKey, in);
        System.out.println(new String(decryptedMessage));

        out.close();
        in.close();
    }

    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hashedPassword = digest.digest(password.getBytes());
        return hashedPassword;
    }

    private static byte[] generateNonce() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[SALT_LENGTH];
        random.nextBytes(nonce);
        return nonce;
    }

    private static byte[] generateProof(String username, String password, byte[] salt, byte[] clientNonce, byte[] serverNonce) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(username.getBytes());
        digest.update(password.getBytes());
        digest.update(salt);
        digest.update(clientNonce);
        digest.update(serverNonce);
        byte[] proof = digest.digest();
        return proof;
    }

    private static byte[] generateKey(String username, String password, byte[] salt, byte[] clientNonce, byte[] serverNonce) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes());
        digest.update(salt);
        digest.update(clientNonce);
        digest.update(serverNonce);
        byte[] key = digest.digest();
        return key;
    }

    private static byte[] encrypt(byte[] key, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        return encryptedMessage;
    }

    private static byte[] decrypt(byte[] key, byte[] encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return decryptedData;
    }

    private static byte[] decrypt(byte[] key, DataInputStream in) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        int length = in.readInt();
        byte[] encryptedData = new byte[length];
        in.readFully(encryptedData);
        byte[] decryptedData = decrypt(key, encryptedData);
        return decryptedData;
    }
}

