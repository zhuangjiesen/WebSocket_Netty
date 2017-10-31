package com.java.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanHelper implements ApplicationContextAware {
	
	
	private static ApplicationContext context;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		context=applicationContext;
	}
	
	
	public static ApplicationContext getContext(){
		return context;
	}

}
