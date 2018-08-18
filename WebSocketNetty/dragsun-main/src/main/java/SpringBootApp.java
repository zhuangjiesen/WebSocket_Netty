
import com.jason.core.mybatis.AppMapperFactoryBean;
import com.jason.core.mybatis.DataInterceptor;
import com.jason.core.mybatis.MybatisDao;
import com.jason.core.resolver.JsonMappingJackson2HttpMessageConverter;
import com.jason.core.web.CorsFilter;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;


/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/24
 */
@SpringBootApplication
@EnableWebMvc
@MapperScan(basePackages = {"com.jason.mapper"}
        , factoryBean = AppMapperFactoryBean.class
)
@ComponentScan(basePackages = {"com.jason"})
@ImportResource(locations={"classpath:applicationContext.xml"})
@org.springframework.context.annotation.Configuration
@EnableAspectJAutoProxy(exposeProxy = true ,proxyTargetClass = true)
public class SpringBootApp  extends WebMvcConfigurerAdapter implements ServletContextInitializer , CommandLineRunner {


    public static void main( String[] args )
    {
        SpringApplication.run(SpringBootApp.class, args);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
    }


    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        //添加解决跨域的filter
        CorsFilter corsFilter = new CorsFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter);
        registration.addUrlPatterns("/*");
        registration.setName("testFilter");
        registration.setOrder(1);
        return registration;
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new JsonMappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("CommandLineRunner... run()..... ");
    }
}
