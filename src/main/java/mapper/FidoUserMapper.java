package mapper;

import pojo.FidoUser;

public interface FidoUserMapper {
    FidoUser getUserByID(int userId);

    FidoUser getUserByName(String userName);

    FidoUser getUserByHandle(byte[] userHandle);

    void insertNewUser(FidoUser fidoUser);
}
