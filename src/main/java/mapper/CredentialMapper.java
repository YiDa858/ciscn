package mapper;

import org.apache.ibatis.annotations.Param;
import pojo.Credential;

import java.util.List;

public interface CredentialMapper {
    Credential getCredentialById(int id);

    List<Credential> getCredentialByCredentialId(byte[] credentialId);

    List<Credential> getCredentialByUserId(int userId);

    List<Credential> getCredentialByUserHandle(byte[] userHandle);

    Credential getCredentialByPublicKeyCose(byte[] publicKeyCose);

    List<Credential> getCredentialsByUserName(String userName);

    Credential getCredentialByCredentialIdAndUserHandle(byte[] credentialId, byte[] userHandle);

    void insertNewCredential(@Param("credentialId") byte[] credentialId,
                             @Param("userId") int userId,
                             @Param("userHandle") byte[] userHandle,
                             @Param("publicKeyCose") byte[] publicKeyCose,
                             @Param("signatureCount") int signatureCount);
}
