package service;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import mapper.CredentialMapper;
import mapper.FidoUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pojo.Credential;
import pojo.FidoUser;
import util.CredentialRepositoryImpl;
import util.SqlSessionFactoryUtils;

import java.util.ArrayList;
import java.util.List;

public class FidoService {
    SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();
    RelyingPartyIdentity relyingPartyIdentity;
    RelyingParty rp;

    /**
     * 构造器
     * 实例化RelyingParty
     * RelyingParty类是该库的主要入口点。可以使用它的生成器方法来实例化它，并将CredentialRepositoryImpl()实现
     */
    public FidoService() {
        relyingPartyIdentity = RelyingPartyIdentity.builder()
                .id("FidoServerID")  // Set this to a parent domain that covers all subdomains
                // where users' credentials should be valid
                .name("FidoServer")
                .build();
        rp = RelyingParty.builder()
                .identity(relyingPartyIdentity)
                .credentialRepository(new CredentialRepositoryImpl())
                .build();
    }

    /**
     * 通过username判断用户是否注册
     *
     * @param username 用户名
     * @return boolean 若用户名已被注册则返回true，用户名未被注册则返回false
     */
    public boolean isRegistered(String username) {
        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        // 通过用户名查询用户
        FidoUser user = fidoUserMapper.getUserByName(username);
        System.out.println("[+] service.FidoService.isRegistered: " + user);

        // 结束session
        sqlSession.close();

        return (user != null);
    }

    /**
     * 注册Fido用户
     *
     * @param username 用户名
     */
    public void RegisterFidoUser(String username, byte[] userHandle) {
        // 判断用户名是否已经注册过
        if (isRegistered(username)) {
            System.out.println("[+] service.FidoService.RegisterFidoUser: Username " + username + " is already in use.");
            return;
        }


        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        // TODO: 为用户创建凭证

        // 新建FidoUser对象并注册
        FidoUser fidoUser = new FidoUser();
        fidoUser.setUserName(username);
        fidoUser.setUserHandle(userHandle);

        fidoUserMapper.insertNewUser(fidoUser);
        System.out.println("[+] service.FidoService.RegisterFidoUser: " + fidoUser);

        // 提交事务
        try {
            sqlSession.commit();
        } catch (Exception e) {
            System.out.println("[-] service.FidoService.RegisterFidoUser: " + e);
        }

        // 结束session
        sqlSession.close();
    }

    /**
     * 通过用户名获取对应的凭证列表
     *
     * @param username 用户名
     * @return 凭证列表
     */
    public List<byte[]> getCredentialIdByName(String username) {
        // 判断用户名是否存在
        if (isRegistered(username)) {
            System.out.println("[+] service.FidoService.getCredentialIdByName: Username " + username + " doesn't exist.");
            return null;
        }

        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        CredentialMapper credentialMapper = sqlSession.getMapper(CredentialMapper.class);

        // 查询用户名对应的凭证对象
        List<Credential> credentials = credentialMapper.getCredentialsByUserName(username);

        // 获取对应的凭证
        List<byte[]> credentialIds = new ArrayList<>();
        for (Credential credential : credentials) {
            System.out.println("[+] service.FidoService.getCredentialIdByName: " + credential.toString());
            credentialIds.add(credential.getCredentialId());
        }

        // 结束session
        sqlSession.close();

        return credentialIds;
    }

    /**
     * 通过用户名获取对应的用户handle
     *
     * @param username 用户名
     * @return 用户handle
     */
    public byte[] getUserHandleByUserName(String username) {
        // 判断用户名是否存在
        if (isRegistered(username)) {
            System.out.println("[+] service.FidoService.getIdByName: Username " + username + " doesn't exist.");
            return null;
        }

        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        // 查询用户名对应的FidoUser对象
        FidoUser user = fidoUserMapper.getUserByName(username);
        System.out.println("[+] service.FidoService.getIdByName: " + user);

        // 结束session
        sqlSession.close();

        return user.getUserHandle();
    }

    /**
     * 通过用户handle获取对应的用户名
     *
     * @param userHandle 用户的handle
     * @return 用户名
     */
    public String getUserNameByUserHandle(byte[] userHandle) {
        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        // 查询handle对应的FidoUser对象
        FidoUser user = fidoUserMapper.getUserByHandle(userHandle);
        System.out.println("[+] service.FidoService.getIdByName: " + user);

        // 结束session
        sqlSession.close();

        return user.getUserName();
    }

    /**
     * 通过凭证id和用户handle得到对应唯一的凭证
     *
     * @param credentialId 凭证id
     * @param userHandle   用户handle
     * @return 唯一凭证
     */
    public Credential getCredentialByCredentialIdAndUserHandle(byte[] credentialId, byte[] userHandle) {
        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        CredentialMapper credentialMapper = sqlSession.getMapper(CredentialMapper.class);

        // 查询对应的凭证
        Credential credential = credentialMapper.getCredentialByCredentialIdAndUserHandle(credentialId, userHandle);

        // 结束session
        sqlSession.close();

        return credential;
    }

    /**
     * 获取凭证id对应的所有凭证
     *
     * @param credentialId 凭证id
     * @return 凭证id对应的凭证列表
     */
    public List<Credential> getCredentialsByCredentialId(byte[] credentialId) {
        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        CredentialMapper credentialMapper = sqlSession.getMapper(CredentialMapper.class);

        // 查询凭证id对应的所有凭证
        List<Credential> credentialList = credentialMapper.getCredentialByCredentialId(credentialId);

        // 结束session
        sqlSession.close();

        return credentialList;
    }

    public void registerNewUser() {
        rp.startRegistration(StartRegistrationOptions.builder()
                .user(
                        findExistingUser("alice")
                                .orElseGet(() -> {
                                    byte[] userHandle = new byte[64];
                                    random.nextBytes(userHandle);
                                    return UserIdentity.builder()
                                            .name("alice")
                                            .displayName("Alice Hypothetical")
                                            .id(new ByteArray(userHandle))
                                            .build();
                                })
                )
                .build());
    }
}
