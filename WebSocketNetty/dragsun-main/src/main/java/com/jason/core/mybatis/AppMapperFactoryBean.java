package com.jason.core.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/18
 */
public class AppMapperFactoryBean extends MapperFactoryBean {


    public AppMapperFactoryBean() {
    }

    public AppMapperFactoryBean(Class mapperInterface) {
        super(mapperInterface);
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
    }

    @Override
    protected void initDao() throws Exception {
        super.initDao();



    }
}
