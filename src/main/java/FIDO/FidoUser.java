package FIDO;

public class FidoUser {
    /**
     * FIDO认证的用户类，保存用户的认证信息，包括用户名name与密码password
     */
    private int userid;
    private String userName;
    private String userPubKey;

    public int getUserid() {
        return userid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPubKey() {
        return userPubKey;
    }

    public FidoUser(String userName, String userPubKey) {
        this.userName = userName;
        this.userPubKey = userPubKey;
    }
    // TODO: 这里的逻辑应该有点问题，需要再改改
}
