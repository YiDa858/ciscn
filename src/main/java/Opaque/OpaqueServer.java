package Opaque;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class OpaqueServer {

    private static final int PORT = 8888;

    private static final Map<String, byte[]> passwordHashes = new HashMap<>();

    private static final Map<String, byte[]> serverNonces = new HashMap<>();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[+] Server is listening on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("[+] Accepted connection from " + clientSocket.getInetAddress().getHostAddress());
            // 与客户端成功建立连接，开始Opaque协议
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String username;
        private byte[] salt;
        private byte[] passwordHash;
        private byte[] serverNonce;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                // op为1时进行注册操作
                int op = in.readInt();
                if (op == 1) {
                    username = in.readUTF();
                    salt = generateSalt();
                    passwordHash = register(username, in.readUTF(), salt);
                    out.write(salt.length);
                    out.write(salt);
                    out.write(passwordHash.length);
                    out.write(passwordHash);
                    out.flush();
                    System.out.println("Registered user " + username);
                }
                // op为2时进行登录操作
                else if (op == 2) {
                    username = in.readUTF();
                    salt = in.readNBytes(in.readInt());
                    passwordHash = passwordHashes.get(username);
                    if (passwordHash == null) {
                        out.writeInt(1);
                        out.flush();
                        return;
                    }
                    serverNonce = generateNonce();
                    serverNonces.put(username, serverNonce);
                    out.writeInt(0);
                    out.write(serverNonce.length);
                    out.write(serverNonce);
                    out.flush();
                    byte[] proof = decrypt(passwordHash, decrypt(generateKey(in.readUTF(), passwordHash, salt, serverNonce), in));
                    if (checkProof(username, proof)) {
                        byte[] sharedSecret = generateKey(in.readUTF(), passwordHash, salt, serverNonce);
                        byte[] encryptedMessage = encrypt(sharedSecret, "Welcome, " + username + "!");
                        byte[] encryptedKey = encrypt(passwordHash, sharedSecret);
                        out.writeInt(0);
                        out.writeInt(encryptedMessage.length);
                        out.write(encryptedMessage);
                        out.writeInt(encryptedKey.length);
                        out.write(encryptedKey);
                        out.flush();
                        System.out.println("Logged in user " + username);
                    } else {
                        out.writeInt(1);
                        out.flush();
                        System.out.println("Invalid proof for user " + username);
                    }
                }
                // Invalid operation
                else {
                    out.writeInt(-1);
                    out.flush();
                }
                clientSocket.close();
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        }

        private byte[] generateSalt() {
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            return salt;
        }

        private byte[] generateNonce() {
            byte[] nonce = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(nonce);
            return nonce;
        }

        private byte[] register(String username, String password, byte[] salt) throws NoSuchAlgorithmException {
            byte[] passwordHash = hashPassword(password, salt);
            passwordHashes.put(username, passwordHash);
            return passwordHash;
        }

        private boolean checkProof(String username, byte[] proof) throws NoSuchAlgorithmException {
            byte[] expectedProof = hash(username, serverNonce, passwordHashes.get(username));
            return MessageDigest.isEqual(proof, expectedProof);
        }

        private byte[] generateKey(String clientNonce, byte[] passwordHash, byte[] salt, byte[] serverNonce) throws NoSuchAlgorithmException {
            return hash(clientNonce, serverNonce, passwordHash, salt);
        }

        private byte[] decrypt(byte[] key, DataInputStream in) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] encrypted = in.readNBytes(in.readInt());
            return cipher.doFinal(encrypted);
        }

        private byte[] encrypt(byte[] key, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(message.getBytes());
        }

        private byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return digest.digest(password.getBytes());
        }

        private byte[] hash(String... inputs) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String input : inputs) {
                digest.update(input.getBytes());
            }
            return digest.digest();
        }
    }
}


