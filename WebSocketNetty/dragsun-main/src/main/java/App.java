import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{

    private static ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        init();
        testLog();
    }



    public static void testLog(){
        System.out.println("==================");
        logger.trace("trace log");
        logger.warn("warn log");
        logger.debug("debug log");
        logger.info("info log");
        logger.error("error log");
    }


    public static void init(){


        applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");

    }
}
