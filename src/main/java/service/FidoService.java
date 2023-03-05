package service;

import mapper.CredentialMapper;
import mapper.FidoUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pojo.Credential;
import pojo.FidoUser;
import util.SqlSessionFactoryUtils;

import java.util.ArrayList;
import java.util.List;

public class FidoService {
    SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    public FidoService() {
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
    public void RegisterFidoUser(String username) {
        // 判断用户名是否已经注册过
        if (isRegistered(username)) {
            System.out.println("[+] service.FidoService.RegisterFidoUser: Username " + username + " is already in use.");
            return;
        }

        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        // 新建FidoUser对象并注册
        FidoUser fidoUser = new FidoUser(username);
        fidoUserMapper.insertNewUser(fidoUser);
        System.out.println("[+] service.FidoService.RegisterFidoUser: " + fidoUser);

        // TODO: 为用户创建凭证

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
        List<Credential> credentials = credentialMapper.getCredentialsByName(username);

        // 获取对应的凭证
        List<byte[]> credentialIds = new ArrayList<>();
        for (Credential credential : credentials) {
            System.out.println("[+] service.FidoService.getCredentialIdByName: " + credential.toString());
            credentialIds.add(credential.getPublicKey());
        }

        // 结束session
        sqlSession.close();

        return credentialIds;
    }

    /**
     * 通过用户名获取对应的id，从而获取用户对应的唯一句柄
     *
     * @param username 用户名
     * @return 用户id
     */
    public int getIdByName(String username) {
        // 判断用户名是否存在
        if (isRegistered(username)) {
            System.out.println("[+] service.FidoService.getIdByName: Username " + username + " doesn't exist.");
            return -1;
        }

        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        // 查询用户名对应的FidoUser对象
        FidoUser user = fidoUserMapper.getUserByName(username);
        System.out.println("[+] service.FidoService.getIdByName: " + user);

        // 结束session
        sqlSession.close();

        return user.getId();
    }
}
