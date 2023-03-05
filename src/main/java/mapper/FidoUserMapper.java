package mapper;

import pojo.FidoUser;

import java.util.List;

public interface FidoUserMapper {
    List<FidoUser> getUserByID(int userid);

    FidoUser getUserByName(String username);

    void insertNewUser(FidoUser fidoUser);
}
