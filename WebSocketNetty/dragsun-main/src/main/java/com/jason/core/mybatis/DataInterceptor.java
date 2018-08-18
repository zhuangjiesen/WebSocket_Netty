package com.jason.core.mybatis;


import com.jason.model.BaseObject;
import com.jason.util.ReflectUtil;
import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.util.Properties;


/**
 *   @param
 * @Author: zhuangjiesen
 * @Date: Created in 2018/6/28
 * @description 不在拦截器中计算总数，影响效率
 *          分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。 利用拦截器实现Mybatis分页的原理：
 *          要利用JDBC对数据库进行操作就必须要有一个对应的Statement对象
 *          ，Mybatis在执行Sql语句前就会产生一个包含Sql语句的Statement对象，而且对应的Sql语句
 *          是在Statement之前产生的，所以我们就可以在它生成Statement之前对用来生成Statement的Sql语句下手
 *          。在Mybatis中Statement语句是通过RoutingStatementHandler对象的
 *          prepare方法生成的。所以利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法
 *          ，然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句，之后再调用
 *          StatementHandler对象的prepare方法，即调用invocation.proceed()。
 *          对于分页而言，在拦截器里面我们还需要做的一个操作就是统计满足当前条件的记录一共有多少
 *          ，这是通过获取到了原始的Sql语句后，把它改为对应的统计语句再利用Mybatis封装好的参数和设
 *          置参数的功能把Sql语句中的参数进行替换，之后再执行查询记录数的Sql语句进行总记录数的统计。
 */
@Slf4j
@Component
@Intercepts({@Signature(type =StatementHandler.class, method = "prepare", args ={Connection.class,Integer.class })})
public class DataInterceptor implements Interceptor, ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // 对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler，
        // BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，
        // SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，而CallableStatementHandler是
        // 处理CallableStatement的。Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个
        // StatementHandler类型的delegate属性，RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、
        // PreparedStatementHandler或CallableStatementHandler，在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。
        // 我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候
        // 是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。
        Object result = null;
        result = invocation.proceed();

        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            final RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();


            // 获取到当前StatementHandler的
            // boundSql，这里不管是调用handler.getBoundSql()还是直接调用delegate.getBoundSql()结果是一样的，因为之前已经说过了
            // RoutingStatementHandler实现的所有StatementHandler接口方法里面都是调用的delegate对应的方法。
            final StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
            if (delegate instanceof PreparedStatementHandler) {
                PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler)delegate;
                Object params = preparedStatementHandler.getParameterHandler().getParameterObject();
                if (params instanceof BaseObject) {
                    BaseObject baseObject = (BaseObject)params;
                    System.out.println(" key1 : " + baseObject.getId());

                }
            }



            System.out.println(delegate.getClass().getName());
            final BoundSql boundSql = delegate.getBoundSql();
            String sql = boundSql.getSql();
            final Object parameterObject = boundSql.getParameterObject();
            if (parameterObject instanceof BaseObject) {
                BaseObject baseObject = (BaseObject)parameterObject;
                System.out.println(" key2 : " + baseObject.getId());

            }

        }

        if (result instanceof HikariProxyPreparedStatement) {
            HikariProxyPreparedStatement preparedStatement = (HikariProxyPreparedStatement)result;


        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }
}
