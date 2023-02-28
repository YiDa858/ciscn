public class OpaqueUser {
    private String name;
    private String password;

    /**
     * 测试使用的构造方法
     */
    public OpaqueUser() {
        this.name = "liu";
        this.password = "yaowen";
    }

    /**
     * OpaqueUser类构造方法
     *
     * @param name     用户名
     * @param password 密码
     */
    public OpaqueUser(String name, String password) {
        // 这里的逻辑不清楚有没有问题，是要使用这个password密码进行用户注册？？
        this.name = name;
        this.password = password;
    }

    /**
     * 通过FIDO用户的相关信息获取OPAQUE用户的相关信息
     *
     * @param FidoUser FIDO用户对应的FidoUser对象
     * @return 返回OPAQUE用户对应的OpaqueUser对象
     */

    public OpaqueUser getUserInfo(FidoUser FidoUser) {
        // TODO: 在数据库中通过FidoUser的用户名查询OPAQUE用户的用户名，进而获取相关信息
        return new OpaqueUser();
    }
}
