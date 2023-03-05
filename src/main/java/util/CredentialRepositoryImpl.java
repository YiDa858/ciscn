package util;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import service.FidoService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CredentialRepositoryImpl implements CredentialRepository {
    private FidoService service = new FidoService();
    /**
     * 获取使用给定用户名注册给用户的所有凭据的凭据ID
     * 成功注册后，RegistrationResult.getKeyId()方法返回一个适合包含在此集合中的值
     * Get the credential IDs of all credentials registered to the user with the given username
     * After a successful registration ceremony, the RegistrationResult.getKeyId() method returns a value suitable for inclusion in this set.
     *
     * @param username
     * @return
     */
    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        byte[] credentialIds = service.getCredentialIdByName(username);
        Set<PublicKeyCredentialDescriptor> result = new HashSet<>();
        for (byte[] credentialId : credentialIds) {
            result.add(new PublicKeyCredentialDescriptor(
                    PublicKeyCredentialType.PUBLIC_KEY, credentialId, null));
        }
        return result;
    }

    /**
     * 获取与给定用户名对应的用户句柄-getUsernameForUserHandle（ByteArray）的倒数
     * 用于根据用户名查找用户句柄，用于已经给定用户名的身份验证仪式
     * Get the user handle corresponding to the given username - the inverse of getUsernameForUserHandle(ByteArray).
     * Used to look up the user handle based on the username, for authentication ceremonies where the username is already given.
     *
     * @param username
     * @return
     */
    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.empty();
    }

    /**
     * 获取与给定用户句柄对应的用户名-getUserHandleForUsername（String）的倒数
     * 用于基于用户句柄查找用户名，用于无用户名身份验证仪式
     * Get the username corresponding to the given user handle - the inverse of getUserHandleForUsername(String).
     * Used to look up the username based on the user handle, for username-less authentication ceremonies.
     *
     * @param userHandle
     * @return
     */
    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return Optional.empty();
    }

    /**
     * 查找注册给给定用户的给定凭证的公钥和存储的签名计数
     * 返回的RegisteredCredential预期寿命不长。它可以直接从数据库中读取，也可以从其他组件中组装
     * Look up the public key and stored signature count for the given credential registered to the given user.
     * The returned RegisteredCredential is not expected to be long-lived. It may be read directly from a database or assembled from other components.
     *
     * @param credentialId
     * @param userHandle
     * @return
     */
    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return Optional.empty();
    }

    /**
     * 查找具有给定凭据ID的所有凭据，无论它们注册到哪个用户
     * 这用于拒绝重复凭据ID的注册。因此，在正常情况下，该方法只应返回零个或一个凭证（这是预期结果，而不是接口要求）
     * Look up all credentials with the given credential ID, regardless of what user they're registered to.
     * This is used to refuse registration of duplicate credential IDs. Therefore, under normal circumstances this method should only return zero or one credential (this is an expected consequence, not an interface requirement).
     *
     * @param credentialId
     * @return
     */
    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return null;
    }
}
