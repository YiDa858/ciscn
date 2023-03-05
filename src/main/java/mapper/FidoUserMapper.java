package mapper;

import pojo.FidoUser;

import java.util.List;

public interface FidoUserMapper {
    List<FidoUser> getUserByID(int userid);
    void insertNewUser(FidoUser fidoUser);
}
