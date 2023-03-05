package pojo;

import java.util.Arrays;

public class Credential {
    private int id;
    private int userId;
    private byte[] publicKey;

    public Credential(int userId, byte[] publicKey) {
        this.userId = userId;
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "id=" + id +
                ", userId=" + userId +
                ", publicKey=" + Arrays.toString(publicKey) +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }
}
