package pojo;

import java.util.Arrays;

public class FidoUser {
    private int id;
    private String username;

    public FidoUser(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "FidoUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
