package pojo;

import java.util.Arrays;

public class FidoUser {
    private int userId;
    private String userName;
    private byte[] userHandle;

    public FidoUser() {
    }

    @Override
    public String toString() {
        return "FidoUser{" +
                "userId=" + userId +
                ", username='" + userName + '\'' +
                ", userHandle=" + Arrays.toString(userHandle) +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(byte[] userHandle) {
        this.userHandle = userHandle;
    }
}
