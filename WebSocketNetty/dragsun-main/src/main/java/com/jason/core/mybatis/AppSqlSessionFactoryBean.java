package com.jason.core.mybatis;

import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/18
 */
@Component
public class AppSqlSessionFactoryBean extends SqlSessionFactoryBean implements ApplicationContextAware{


    private ApplicationContext applicationContext;



    @Autowired
    private DataSource dataSource;

    @Autowired
    private MybatisProperties mybatisProperties;


    @Override
    public void setDataSource(DataSource ds) {
        super.setDataSource(dataSource);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setDataSource(dataSource);
        super.setMapperLocations(mybatisProperties.resolveMapperLocations());
        super.afterPropertiesSet();
        MybatisDao mybatisDao = this.applicationContext.getBean(MybatisDao.class);

        Configuration configuration = mybatisDao.getSqlSession().getConfiguration();
        Collection<XMLStatementBuilder> XMLStatementBuilderList = configuration.getIncompleteStatements();
//        configuration.
//        Jdbc3KeyGenerator
        Collection<MappedStatement> statementCollection = configuration.getMappedStatements();
        if (statementCollection != null && statementCollection.size() > 0) {
            statementCollection.forEach(new Consumer<MappedStatement>() {
                @Override
                public void accept(MappedStatement mappedStatement) {
                    SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
                    if (sqlCommandType.name() == SqlCommandType.INSERT.name()) {
                        KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
                        if (keyGenerator == NoKeyGenerator.INSTANCE) {
//                            configuration.setUseGeneratedKeys(true);
//                            configuration.addKeyGenerator(mappedStatement.getId() ,Jdbc3KeyGenerator.INSTANCE );


                            System.out.println(" not keyGenerator ... ");
                        } else if (keyGenerator == Jdbc3KeyGenerator.INSTANCE) {
                            System.out.println(" has Jdbc3KeyGenerator ... ");
                        }
                        System.out.println();

                    }
                }
            });
        }

        mybatisDao.getSqlSession().getConfiguration().addInterceptor(new DataInterceptor());

    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }
}
