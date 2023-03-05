package service;

import org.apache.ibatis.session.SqlSessionFactory;
import util.SqlSessionFactoryUtils;

public class OpaqueService {
    SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();
}
