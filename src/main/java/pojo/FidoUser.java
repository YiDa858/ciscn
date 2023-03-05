package pojo;

public class FidoUser {
    private Integer userid;
    private String userName;
    private String userPubKey;

    public FidoUser(String userName, String userPubKey) {
        this.userName = userName;
        this.userPubKey = userPubKey;
    }

    @Override
    public String toString() {
        return "FidoUser{" +
                "userid=" + userid +
                ", userName='" + userName + '\'' +
                ", userPubKey='" + userPubKey + '\'' +
                '}';
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPubKey() {
        return userPubKey;
    }

    public void setUserPubKey(String userPubKey) {
        this.userPubKey = userPubKey;
    }
}
