package FIDO.mapper;

import FIDO.FidoUser;

import java.util.List;

public interface UserMapper {
    List<FidoUser> getUserByID(int userid);
    void insertNewUser(FidoUser fidoUser);
}
