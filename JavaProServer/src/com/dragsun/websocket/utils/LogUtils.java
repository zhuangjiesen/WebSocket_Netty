package com.dragsun.websocket.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuangjiesen on 2017/12/5.
 */
public class LogUtils {




    // logMap 存放key为class value为Log的缓存
    private static final Map<Class<?>, Logger> logMap	= new HashMap<Class<?>, Logger>();

    private static Logger getLog(Object obj) {
        Class<?> clazz = null;
        if(obj == null){
            throw new IllegalArgumentException("记录日志对象不能为空！");
        }
        if (obj instanceof Class<?>) {
            clazz = (Class<?>)obj;
        } else {
            clazz = obj.getClass();
        }
        Logger logger = logMap.get(clazz);
        if(logger == null){
            logger = LoggerFactory.getLogger(clazz);
            logMap.put(clazz,logger );
        }
        return logger;
    }

    public static void logInfo(Object obj, Object message) {
        Logger logger = getLog(obj);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message));
        }
    }

    public static void logInfo(Object obj, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message1, message2));
        }
    }

    public static void logInfo(Object obj, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message));
        }
    }

    public static void logError(Object obj, Object message) {
        Logger logger = getLog(obj);
        if(logger.isErrorEnabled()){
            if (message instanceof Throwable) {
                logger.error("", (Throwable)message);
            } else {
                logger.error(bufferToString(message));
            }
        }
    }

    public static void logError(Object obj, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isErrorEnabled()){
            if(message2 instanceof Throwable){
                logger.error(bufferToString(message1),(Throwable)message2);
            }else{
                logger.error(bufferToString(message1, message2));
            }
        }
    }

    public static void logError(Object obj, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message));
        }
    }

    public static void logDebug(Object obj, Object message) {
        Logger logger = getLog(obj);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message));
        }
    }

    public static void logDebug(Object obj, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message1, message2));
        }
    }

    public static void logDebug(Object obj, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message));
        }
    }

    public static void logWarn(Object obj, Object message) {
        Logger logger = getLog(obj);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message));
        }
    }

    public static void logWarn(Object obj, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message1, message2));
        }
    }



    public static void logWarn(Object obj, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message));
        }
    }




    public static void logInfo(Object obj, Throwable e, Object message) {
        Logger logger = getLog(obj);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message), e);
        }
    }





    public static void logInfo(Object obj, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message1, message2), e);
        }
    }





    public static void logInfo(Object obj, Throwable e, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message), e);
        }
    }



    public static void logError(Object obj, Throwable e, Object message) {
        Logger logger = getLog(obj);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message), e);
        }
    }


    public static void logError(Object obj, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message1, message2), e);
        }
    }


    public static void logError(Object obj, Throwable e, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message), e);
        }
    }


    public static void logDebug(Object obj, Throwable e, Object message) {
        Logger logger = getLog(obj);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message), e);
        }
    }


    public static void logDebug(Object obj, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message1, message2), e);
        }
    }


    public static void logDebug(Object obj, Throwable e, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message), e);
        }
    }



    public static void logWarn(Object obj, Throwable e, Object message) {
        Logger logger = getLog(obj);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message), e);
        }
    }



    public static void logWarn(Object obj, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(obj);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message1, message2), e);
        }
    }



    public static void logWarn(Object obj, Throwable e, Object... message) {
        Logger logger = getLog(obj);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message), e);
        }
    }



    public static void logInfo(Class<?> clazz, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message));
        }
    }


    public static void logInfo(Class<?> clazz, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message1, message2));
        }
    }


    public static void logInfo(Class<?> clazz, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message));
        }
    }


    public static void logError(Class<?> clazz, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message));
        }
    }


    public static void logError(Class<?> clazz, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message1, message2));
        }
    }


    public static void logError(Class<?> clazz, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message));
        }
    }


    public static void logDebug(Class<?> clazz, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message));
        }
    }


    public static void logDebug(Class<?> clazz, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message1, message2));
        }
    }


    public static void logDebug(Class<?> clazz, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message));
        }
    }


    public static void logWarn(Class<?> clazz, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message));
        }
    }


    public static void logWarn(Class<?> clazz, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message1, message2));
        }
    }


    public static void logWarn(Class<?> clazz, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message));
        }
    }


    public static void logInfo(Class<?> clazz, Throwable e, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message), e);
        }
    }


    public static void logInfo(Class<?> clazz, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message1, message2), e);
        }
    }


    public static void logInfo(Class<?> clazz, Throwable e, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isInfoEnabled()){
            logger.info(bufferToString(message), e);
        }
    }



    public static void logError(Class<?> clazz, Throwable e, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message), e);
        }
    }



    public static void logError(Class<?> clazz, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message1, message2), e);
        }
    }



    public static void logError(Class<?> clazz, Throwable e, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isErrorEnabled()){
            logger.error(bufferToString(message), e);
        }
    }



    public static void logDebug(Class<?> clazz, Throwable e, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message), e);
        }
    }



    public static void logDebug(Class<?> clazz, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message1, message2), e);
        }
    }



    public static void logDebug(Class<?> clazz, Throwable e, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isDebugEnabled()){
            logger.debug(bufferToString(message), e);
        }
    }




    public static void logWarn(Class<?> clazz, Throwable e, Object message) {
        Logger logger = getLog(clazz);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message), e);
        }
    }



    public static void logWarn(Class<?> clazz, Throwable e, Object message1, Object message2) {
        Logger logger = getLog(clazz);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message1, message2), e);
        }
    }



    public static void logWarn(Class<?> clazz, Throwable e, Object... message) {
        Logger logger = getLog(clazz);
        if(logger.isWarnEnabled()){
            logger.warn(bufferToString(message), e);
        }
    }


    public static String bufferToString(Object object) {
        if (object instanceof Throwable) {
            StringBuilder sb = new StringBuilder();
            Throwable throwable = ((Throwable)object);
            sb.append("异常描述：").append(throwable.getMessage()).append("\n堆栈信息：");
            for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                sb.append(stackTraceElement).append("\n");
            }
            return sb.toString();
        } else {
            return object == null?"NULL":object.toString();
        }
    }

    private static String bufferToString(Object object1, Object object2) {
        return bufferToString(object1) + bufferToString(object2);
    }

    private static String bufferToString(Object... objects) {
        StringBuilder sb = new StringBuilder();
        if (objects != null) {
            for (Object object : objects) {
                sb.append(bufferToString(object));
            }
        } else {
            sb.append("NULL");
        }
        return sb.toString();
    }

}
