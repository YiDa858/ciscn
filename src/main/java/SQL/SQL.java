package SQL;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SQL {
    public static void main(String[] args) throws IOException {
        // 加载MyBatis的核心配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 获取SqlSessionFactory对象，用于执行sql
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 执行sql
        // List<User> users = sqlSession.selectList("test.selectAll");

        // System.out.println(users);

        // 释放资源
        sqlSession.close();
    }
}
