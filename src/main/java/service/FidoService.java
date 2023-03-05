package service;

import mapper.FidoUserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import pojo.FidoUser;
import util.SqlSessionFactoryUtils;

import java.util.List;

public class FidoService {
    SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    public boolean isRegisted(int userid) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);
        // TODO:逻辑修改
        List<FidoUser> ans = fidoUserMapper.getUserByID(userid);

        sqlSession.close();

        return ans.isEmpty();
    }

    public void fidoRegister(String userName, String userPubKey) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        FidoUserMapper fidoUserMapper = sqlSession.getMapper(FidoUserMapper.class);

        FidoUser fidoUser = new FidoUser(userName, userPubKey);
        fidoUserMapper.insertNewUser(fidoUser);

        System.out.println("Successfully register a new user: id: " + fidoUser.getUserid() + " /name: " + userName);

        try {
            sqlSession.commit();
        } catch (Exception e) {
            System.out.println("fidoRegister: " + e);
        }

        sqlSession.close();
    }

    public void GetPubkeyById(String username) {
        // TODO:
    }


}
