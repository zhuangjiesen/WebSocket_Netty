import com.jason.core.web.CorsFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/24
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.jason")
@ImportResource(locations={"classpath:applicationContext.xml"})
public class SpringBootApp implements ServletContextInitializer {
    public static void main( String[] args )
    {
        SpringApplication.run(SpringBootApp.class, args);



    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

    }



    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        CorsFilter corsFilter = new CorsFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(corsFilter);
        registration.addUrlPatterns("/*");
        registration.setName("testFilter");
        registration.setOrder(1);
        return registration;
    }







}
