package service;

import mapper.FidoUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pojo.FidoUser;
import util.SqlSessionFactoryUtils;

import java.util.Arrays;

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

    public byte[] getCredentialIdByName(String username) {
        // 判断用户名是否存在
        if (isRegistered(username)) {
            System.out.println("[+] service.FidoService.getCredentialIdByName: Username " + username + " doesn't exist.");
            return null;
        }

        // 通过连接池获取session，并得到对应mapper
        SqlSession sqlSession = sqlSessionFactory.openSession();


    }
}
